package com.linln.admin.stock.controller;

import com.google.common.collect.Lists;
import com.linln.admin.stock.domain.StockOrderInfo;
import com.linln.admin.stock.service.StockOrderInfoService;
import com.linln.admin.stock.validator.StockOrderInfoValid;
import com.linln.admin.stockin.domain.StockinOrderInfoExcel;
import com.linln.admin.warehouse.domain.WarehouseLocation;
import com.linln.admin.warehouse.service.WarehouseLocationService;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.excel.ExcelUtil;
import com.linln.component.shiro.ShiroUtil;
import com.linln.modules.system.domain.User;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author 小懒虫
 * @date 2020/12/11
 */
@Controller
@RequestMapping("/stock/stockOrderInfo")
public class StockOrderInfoController {

    @Autowired
    private StockOrderInfoService stockOrderInfoService;

    @Autowired
    private WarehouseLocationService warehouseLocationService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("stock:stockOrderInfo:index")
    public String index(Model model, StockOrderInfo stockOrderInfo) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();

        // 获取数据列表
        Example<StockOrderInfo> example = Example.of(stockOrderInfo, matcher);
        Page<StockOrderInfo> list = stockOrderInfoService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/stock/stockOrderInfo/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("stock:stockOrderInfo:add")
    public String toAdd() {
        return "/stock/stockOrderInfo/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("stock:stockOrderInfo:edit")
    public String toEdit(@PathVariable("id") StockOrderInfo stockOrderInfo, Model model) {
        model.addAttribute("stockOrderInfo", stockOrderInfo);
        return "/stock/stockOrderInfo/add";
    }

    /**
     * 保存添加/修改的数据
     *
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"stock:stockOrderInfo:add", "stock:stockOrderInfo:edit"})
    @ResponseBody
    public ResultVo save(@Validated StockOrderInfoValid valid, StockOrderInfo stockOrderInfo) {
        // 复制保留无需修改的数据
        if (stockOrderInfo.getId() != null) {
            StockOrderInfo beStockOrderInfo = stockOrderInfoService.getById(stockOrderInfo.getId());
            EntityBeanUtil.copyProperties(beStockOrderInfo, stockOrderInfo);
        }

        // 保存数据
        stockOrderInfoService.save(stockOrderInfo);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("stock:stockOrderInfo:detail")
    public String toDetail(@PathVariable("id") StockOrderInfo stockOrderInfo, Model model) {
        model.addAttribute("stockOrderInfo", stockOrderInfo);
        return "/stock/stockOrderInfo/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("stock:stockOrderInfo:status")
    @ResponseBody
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (stockOrderInfoService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/allocationSubmit")
    @RequiresPermissions("stock:stockOrderInfo:allocationSubmit")
    @ResponseBody
    public ResultVo status(
            @RequestParam(value = "ids", required = false) List<Long> ids,
            @RequestParam(value = "allocationNum", required = false) String allocationNumStr,
            @RequestParam(value = "orderId", required = false) String orderIdStr) {

        Long locationId = ids.get(0);
        Integer allocationNum = Integer.parseInt(allocationNumStr);
        Long orderId = Long.parseLong(orderIdStr);
        WarehouseLocation warehouseLocation = warehouseLocationService.getById(locationId);
        StockOrderInfo stockOrderInfo = stockOrderInfoService.getById(orderId);
        Integer qty = stockOrderInfo.getQty();
        if (qty < allocationNum) {
            return ResultVoUtil.error("库存数小于调拨数，请重新操作");
        }
        Integer surQty = qty - allocationNum;
        // 调拨后qty为0，删除该库存
        if (surQty == 0) {
            stockOrderInfoService.deleteById(stockOrderInfo.getId());
        } else if (surQty > 0) {
            stockOrderInfo.setQty(surQty);
            stockOrderInfoService.updateQtyById(surQty, orderId);
        }
        // 调拨对象赋值
        StockOrderInfo allocationStockOrderInfo = new StockOrderInfo();
        allocationStockOrderInfo.setOrderNo(stockOrderInfo.getOrderNo());
        allocationStockOrderInfo.setModel(stockOrderInfo.getModel());
        allocationStockOrderInfo.setSn(stockOrderInfo.getSn());
        allocationStockOrderInfo.setDn(stockOrderInfo.getDn());
        allocationStockOrderInfo.setPo(stockOrderInfo.getPo());
        allocationStockOrderInfo.setSo(stockOrderInfo.getSo());
        allocationStockOrderInfo.setContractNo(stockOrderInfo.getContractNo());
        allocationStockOrderInfo.setProductDate(stockOrderInfo.getProductDate());
        allocationStockOrderInfo.setWeight(stockOrderInfo.getWeight());
        allocationStockOrderInfo.setVolume(stockOrderInfo.getVolume());
        allocationStockOrderInfo.setGoodsName(stockOrderInfo.getGoodsName());
        allocationStockOrderInfo.setProductType(stockOrderInfo.getProductType());
        allocationStockOrderInfo.setSupplier(stockOrderInfo.getSupplier());
        allocationStockOrderInfo.setInternalNo(stockOrderInfo.getInternalNo());
        allocationStockOrderInfo.setRemark(stockOrderInfo.getRemark());
        allocationStockOrderInfo.setStatus(stockOrderInfo.getStatus());
        // 获取当前用户信息
        User subject = ShiroUtil.getSubject();
        allocationStockOrderInfo.setQty(allocationNum);
        allocationStockOrderInfo.setCreateBy(subject);
        allocationStockOrderInfo.setCreateDate(new Date());
        allocationStockOrderInfo.setUpdateBy(subject);
        allocationStockOrderInfo.setUpdateDate(new Date());
        allocationStockOrderInfo.setLocationId(Long.toString(locationId));
        allocationStockOrderInfo.setLocationNo(warehouseLocation.getCode());
        allocationStockOrderInfo.setRegionId(Long.toString(warehouseLocation.getRegionId()));
        stockOrderInfoService.save(allocationStockOrderInfo);
        return ResultVoUtil.success("调拨成功");
    }

    /**
     * 库存调拨
     */
    @GetMapping("/allocationStock/{orderId}")
    @RequiresPermissions("stock:stockOrderInfo:allocation")
    public String allocationStock(@PathVariable("orderId") Long orderId, WarehouseLocation warehouseLocation, Model model) {
        StockOrderInfo byId = stockOrderInfoService.getById(orderId);
        model.addAttribute("stockOrderInfo", byId);
        model.addAttribute("orderId", orderId);
        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();
        // 获取数据列表
        Example<WarehouseLocation> example = Example.of(warehouseLocation, matcher);
        Page<WarehouseLocation> list = warehouseLocationService.getPageList(example);
        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/stock/stockOrderInfo/allocation";
    }

    /**
     * 导出明细
     */
    @GetMapping("/exportStock/{orderIds}")
    @ResponseBody
    public void exportStock(@PathVariable("orderIds") String orderIds) {

        String[] split = orderIds.split("&");
        List<StockinOrderInfoExcel> orderInfoExcels = Lists.newArrayList();
        for (String orderId : split) {
            StockinOrderInfoExcel orderInfoExcel = new StockinOrderInfoExcel();
            StockOrderInfo orderInfo = stockOrderInfoService.getById(Long.parseLong(orderId));
            BeanUtils.copyProperties(orderInfo, orderInfoExcel);
            orderInfoExcels.add(orderInfoExcel);
        }
        // 导出Excel
        ExcelUtil.exportExcel(StockinOrderInfoExcel.class, orderInfoExcels);
    }
}