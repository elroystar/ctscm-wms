package com.linln.admin.stockout.controller;

import com.google.common.collect.Lists;
import com.linln.admin.stockout.domain.StockoutOrder;
import com.linln.admin.stockout.domain.StockoutOrderInfo;
import com.linln.admin.stockout.domain.StockoutOrderInfoExcel;
import com.linln.admin.stockout.service.StockoutOrderInfoService;
import com.linln.admin.stockout.service.StockoutOrderService;
import com.linln.admin.stockout.validator.StockoutOrderValid;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.excel.ExcelUtil;
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

import java.util.List;

/**
 * @author 小懒虫
 * @date 2020/12/30
 */
@Controller
@RequestMapping("/stockout/stockoutOrder")
public class StockoutOrderController {

    @Autowired
    private StockoutOrderService stockoutOrderService;

    @Autowired
    private StockoutOrderInfoService stockoutOrderInfoService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("stockout:stockoutOrder:index")
    public String index(Model model, StockoutOrder stockoutOrder) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();

        // 获取数据列表
        Example<StockoutOrder> example = Example.of(stockoutOrder, matcher);
        Page<StockoutOrder> list = stockoutOrderService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/stockout/stockoutOrder/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("stockout:stockoutOrder:add")
    public String toAdd() {
        return "/stockout/stockoutOrder/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("stockout:stockoutOrder:edit")
    public String toEdit(@PathVariable("id") StockoutOrder stockoutOrder, Model model) {
        model.addAttribute("stockoutOrder", stockoutOrder);
        return "/stockout/stockoutOrder/add";
    }

    /**
     * 保存添加/修改的数据
     *
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"stockout:stockoutOrder:add", "stockout:stockoutOrder:edit"})
    @ResponseBody
    public ResultVo save(@Validated StockoutOrderValid valid, StockoutOrder stockoutOrder) {
        // 复制保留无需修改的数据
        if (stockoutOrder.getId() != null) {
            StockoutOrder beStockoutOrder = stockoutOrderService.getById(stockoutOrder.getId());
            EntityBeanUtil.copyProperties(beStockoutOrder, stockoutOrder);
        }

        // 保存数据
        stockoutOrderService.save(stockoutOrder);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("stockout:stockoutOrder:detail")
    public String toDetail(@PathVariable("id") StockoutOrder stockoutOrder, Model model) {
        model.addAttribute("stockoutOrder", stockoutOrder);
        return "/stockout/stockoutOrder/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("stockout:stockoutOrder:status")
    @ResponseBody
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (stockoutOrderService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }

    /**
     * 跳转到查看明细
     */
    @GetMapping("/getInfo/{orderNo}")
    public String addInfo(@PathVariable("orderNo") String orderNo, Model model) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();
        StockoutOrderInfo stockoutOrderInfo = new StockoutOrderInfo();
        stockoutOrderInfo.setOutNo(orderNo);

        // 获取数据列表
        Example<StockoutOrderInfo> example = Example.of(stockoutOrderInfo, matcher);
        Page<StockoutOrderInfo> list = stockoutOrderInfoService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/stockout/stockoutOrderInfo/index";
    }

    /**
     * 导出报表
     */
    @GetMapping("/exportExcel/{orderId}")
    @ResponseBody
    public void exportExcel(@PathVariable("orderId") String orderId) {
        StockoutOrder stockoutOrder = stockoutOrderService.getById(Long.parseLong(orderId));
        String orderNo = stockoutOrder.getOrderNo();
        List<StockoutOrderInfo> stockoutOrderInfos = stockoutOrderInfoService.getByOutNo(orderNo);
        List<StockoutOrderInfoExcel> stockoutOrderInfoExcels = Lists.newArrayList();
        for (StockoutOrderInfo orderInfo : stockoutOrderInfos) {
            StockoutOrderInfoExcel stockoutOrderInfoExcel = new StockoutOrderInfoExcel();
            BeanUtils.copyProperties(orderInfo, stockoutOrderInfoExcel);
            stockoutOrderInfoExcels.add(stockoutOrderInfoExcel);
        }
        // 导出Excel
        ExcelUtil.exportExcel(StockoutOrderInfoExcel.class, stockoutOrderInfoExcels);
    }
}