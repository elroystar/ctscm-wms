package com.linln.admin.stockin.controller;

import com.linln.admin.stock.service.StockOrderInfoService;
import com.linln.admin.stockin.domain.StockinOrderInfo;
import com.linln.admin.stockin.domain.StockinOrderInfoExcel;
import com.linln.admin.stockin.service.StockinOrderInfoService;
import com.linln.admin.stockin.service.StockinOrderService;
import com.linln.admin.stockin.validator.StockinOrderInfoValid;
import com.linln.admin.stockout.service.StockoutOrderInfoService;
import com.linln.common.enums.OrderInfoStatusEnum;
import com.linln.common.enums.OrderStatusEnum;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.excel.ExcelUtil;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author 小懒虫
 * @date 2020/12/11
 */
@Controller
@RequestMapping("/stockin/stockinOrderInfo")
public class StockinOrderInfoController {

    @Autowired
    private StockinOrderService stockinOrderService;

    @Autowired
    private StockinOrderInfoService stockinOrderInfoService;

    @Autowired
    private StockOrderInfoService stockOrderInfoService;

    @Autowired
    private StockoutOrderInfoService stockoutOrderInfoService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("stockin:stockinOrderInfo:index")
    public String index(Model model, StockinOrderInfo stockinOrderInfo) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();

        // 获取数据列表
        Example<StockinOrderInfo> example = Example.of(stockinOrderInfo, matcher);
        Page<StockinOrderInfo> list = stockinOrderInfoService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/stockin/stockinOrderInfo/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("stockin:stockinOrderInfo:add")
    public String toAdd() {
        return "/stockin/stockinOrderInfo/add";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add/{orderNo}/{regionId}")
    @RequiresPermissions("stockin:stockinOrderInfo:add")
    public String toAdd(@PathVariable("orderNo") String orderNo, @PathVariable("regionId") String regionId, Model model) {
        StockinOrderInfo stockinOrderInfo = new StockinOrderInfo();
        stockinOrderInfo.setOrderNo(orderNo);
        stockinOrderInfo.setRegionId(regionId);
        model.addAttribute("stockinOrderInfo", stockinOrderInfo);
        return "/stockin/stockinOrderInfo/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("stockin:stockinOrderInfo:edit")
    public String toEdit(@PathVariable("id") StockinOrderInfo stockinOrderInfo, Model model) {
        model.addAttribute("stockinOrderInfo", stockinOrderInfo);
        return "/stockin/stockinOrderInfo/add";
    }

    /**
     * 保存添加/修改的数据
     *
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"stockin:stockinOrderInfo:add", "stockin:stockinOrderInfo:edit"})
    @ResponseBody
    public ResultVo save(@Validated StockinOrderInfoValid valid, StockinOrderInfo stockinOrderInfo) {
        // 复制保留无需修改的数据
        if (stockinOrderInfo.getId() != null) {
            StockinOrderInfo beStockinOrderInfo = stockinOrderInfoService.getById(stockinOrderInfo.getId());
            EntityBeanUtil.copyProperties(beStockinOrderInfo, stockinOrderInfo);
        }

        // 保存数据
        stockinOrderInfoService.save(stockinOrderInfo);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("stockin:stockinOrderInfo:detail")
    public String toDetail(@PathVariable("id") StockinOrderInfo stockinOrderInfo, Model model) {
        model.addAttribute("stockinOrderInfo", stockinOrderInfo);
        return "/stockin/stockinOrderInfo/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("stockin:stockinOrderInfo:status")
    @ResponseBody
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        if ("submit".equals(param)) {
            String orderNo = null;
            // 明细入库
            for (long id : ids) {
                StockinOrderInfo byId = stockinOrderInfoService.getById(id);
                if (!byId.getStatus().equals(OrderInfoStatusEnum.CHECKED.getMessage())) {
                    return ResultVoUtil.error("失败，存在未过检数据");
                }
                orderNo = byId.getOrderNo();
                stockinOrderInfoService.putInStorage(byId);
            }
            // 修改订单状态
            if (stockinOrderInfoService.checkNumByOrderNo(orderNo) > 0) {
                stockinOrderService.updateStatusByOrderNo(OrderStatusEnum.PART.getCode(), orderNo);
            } else {
                stockinOrderService.updateStatusByOrderNo(OrderStatusEnum.ALL.getCode(), orderNo);
            }
            return ResultVoUtil.success("成功");
        } else if ("check".equals(param)) {
            for (Long id : ids) {
                StockinOrderInfo byId = stockinOrderInfoService.getById(id);
                String status = checkOrderStatus(byId, Boolean.TRUE);
                stockinOrderInfoService.updateOrderInfoStatusById(status, id);
            }
            return ResultVoUtil.success("检验成功");
        } else if ("delete".equals(param)) {
            for (long id : ids) {
                stockinOrderInfoService.deleteById(id);
            }
            return ResultVoUtil.success("删除成功");
        } else {
            return ResultVoUtil.error("失败，请重新操作");
        }

    }

    /**
     * 导入明细
     */
    @PostMapping("/upload")
    @ResponseBody
    public ResultVo uploadImage(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) {
        try {
            String orderNo = request.getParameter("orderNo");
            String regionId = request.getParameter("regionId");
            InputStream inputStream = multipartFile.getInputStream();
            List<StockinOrderInfoExcel> orderInfoExcels = ExcelUtil.importExcel(StockinOrderInfoExcel.class, inputStream);
            for (StockinOrderInfoExcel orderInfoExcel : orderInfoExcels) {
                StockinOrderInfo orderInfo = new StockinOrderInfo();
                BeanUtils.copyProperties(orderInfoExcel, orderInfo);
                orderInfo.setOrderNo(orderNo);
                orderInfo.setRegionId(regionId);
                checkOrderStatus(orderInfo, Boolean.FALSE);
                stockinOrderInfoService.save(orderInfo);
            }
            return ResultVoUtil.success();
        } catch (Exception e) {
            return ResultVoUtil.error("导入库位失败");
        }
    }

    private String checkOrderStatus(StockinOrderInfo orderInfo, Boolean isRechecked) {
        String status = OrderInfoStatusEnum.UNCHECKED.getMessage();
        String sn = orderInfo.getSn();
        if (isRechecked) {
            if (StringUtils.isBlank(orderInfo.getRemark())) {
                status = status + ",复检“入库备注”字段为必填项";
            }
        }
        if (StringUtils.isBlank(orderInfo.getModel())) {
            status = status + ",型号/物料号不能为空";
        }
        if (null == orderInfo.getQty() && orderInfo.getQty() <= 0) {
            status = status + ",QTY不能为空";
        }
        if (null == orderInfo.getWeight() || orderInfo.getWeight().compareTo(BigDecimal.ZERO) <= 0) {
            status = status + ",入库重量不能为空";
        }
        if (null == orderInfo.getVolume() || orderInfo.getVolume().compareTo(BigDecimal.ZERO) <= 0) {
            status = status + ",入库体积不能为空";
        }
        if (StringUtils.isBlank(orderInfo.getSupplier())) {
            status = status + ",入库供应商不能为空";
        }
        if (StringUtils.isBlank(orderInfo.getLocationNo())) {
            status = status + ",库位号不能为空";
        }
        if (stockOrderInfoService.checkSn(sn) == 1) {
            status = OrderInfoStatusEnum.UNCHECKED.getMessage() + ",与库存序列号重复";
        }
        if (stockoutOrderInfoService.checkSn(sn) == 1) {
            status = OrderInfoStatusEnum.UNCHECKED.getMessage() + ",与已出库货物序列号重复";
        }
        if (!status.contains(",")) {
            status = OrderInfoStatusEnum.CHECKED.getMessage();
        }
        orderInfo.setStatus(status);
        return status;
    }
}