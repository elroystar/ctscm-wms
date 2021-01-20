package com.linln.admin.stockout.controller;

import com.linln.admin.stock.domain.StockOrderInfo;
import com.linln.admin.stockout.domain.StockoutOrderInfo;
import com.linln.admin.stockout.service.StockoutOrderInfoService;
import com.linln.admin.stockout.service.StockoutPreOrderInfoService;
import com.linln.admin.stockout.validator.StockoutPreOrderInfoValid;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.shiro.ShiroUtil;
import com.linln.modules.system.domain.Region;
import com.linln.modules.system.domain.User;
import com.linln.modules.system.service.RegionService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 小懒虫
 * @date 2020/12/25
 */
@Controller
@RequestMapping("/stockout/stockoutPreOrderInfo")
public class StockoutPreOrderInfoController {

    @Autowired
    private StockoutPreOrderInfoService stockoutPreOrderInfoService;

    @Autowired
    private StockoutOrderInfoService stockoutOrderInfoService;

    @Autowired
    private RegionService regionService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("stockout:stockoutPreOrderInfo:index")
    public String index(Model model, StockOrderInfo stockoutPreOrderInfo, HttpServletRequest request) {

        // 获取库区数据
        String regionId = request.getParameter("regionId");
        if (StringUtils.isBlank(regionId)) {
            User subject = ShiroUtil.getSubject();
            List<Region> region = regionService.getRegionByUserId(Long.toString(subject.getId()));
            regionId = Long.toString(region.get(0).getRegionId());
        }
        stockoutPreOrderInfo.setRegionId(regionId);
        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();

        // 获取数据列表
        Example<StockOrderInfo> example = Example.of(stockoutPreOrderInfo, matcher);
        Page<StockOrderInfo> list = stockoutPreOrderInfoService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/stockout/stockoutPreOrderInfo/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("stockout:stockoutPreOrderInfo:add")
    public String toAdd() {
        return "/stockout/stockoutPreOrderInfo/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("stockout:stockoutPreOrderInfo:edit")
    public String toEdit(@PathVariable("id") StockOrderInfo stockoutPreOrderInfo, Model model) {
        model.addAttribute("stockoutPreOrderInfo", stockoutPreOrderInfo);
        return "/stockout/stockoutPreOrderInfo/add";
    }

    /**
     * 保存添加/修改的数据
     *
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"stockout:stockoutPreOrderInfo:add", "stockout:stockoutPreOrderInfo:edit"})
    @ResponseBody
    public ResultVo save(@Validated StockoutPreOrderInfoValid valid, StockOrderInfo stockoutPreOrderInfo) {
        // 复制保留无需修改的数据
        if (stockoutPreOrderInfo.getId() != null) {
            StockOrderInfo beStockoutPreOrderInfo = stockoutPreOrderInfoService.getById(stockoutPreOrderInfo.getId());
            EntityBeanUtil.copyProperties(beStockoutPreOrderInfo, stockoutPreOrderInfo);
        }

        // 保存数据
        stockoutPreOrderInfoService.save(stockoutPreOrderInfo);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("stockout:stockoutPreOrderInfo:detail")
    public String toDetail(@PathVariable("id") StockOrderInfo stockoutPreOrderInfo, Model model) {
        model.addAttribute("stockoutPreOrderInfo", stockoutPreOrderInfo);
        return "/stockout/stockoutPreOrderInfo/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("stockout:stockoutPreOrderInfo:status")
    @ResponseBody
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (stockoutPreOrderInfoService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }

    /**
     * 确定出库列表
     */
    @GetMapping("/getPreOrderInfo")
    public String index(Model model, StockoutOrderInfo stockoutOrderInfo) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();
        stockoutOrderInfo.setStatus(StatusEnum.FREEZED.getCode());

        // 获取数据列表
        Example<StockoutOrderInfo> example = Example.of(stockoutOrderInfo, matcher);
        Page<StockoutOrderInfo> list = stockoutOrderInfoService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/stockout/stockoutPreOrderInfo/stockoutPre";
    }

    /**
     * 自动出库
     */
    @GetMapping("/outQtyInput/{outType}/{outValue}/{outQtyInput}")
    @ResponseBody
    public ResultVo outQtyInput(@PathVariable("outType") String outType,
                                @PathVariable("outValue") String outValue,
                                @PathVariable("outQtyInput") String outQtyInput) {
        ResultVo resultVo;
        try {
            resultVo = stockoutPreOrderInfoService.outQtyInput(outType, outValue, outQtyInput);
        } catch (Exception e) {
            return ResultVoUtil.error(e.getMessage());
        }
        return resultVo;
    }

    /**
     * 手动qty出库
     */
    @GetMapping("/outQtyTd/{outQtyTdArray}")
    @ResponseBody
    public ResultVo outQtyTd(@PathVariable("outQtyTdArray") String outQtyTdArray) {
        ResultVo resultVo;
        try {
            resultVo = stockoutPreOrderInfoService.outQtyTd(outQtyTdArray);
        } catch (Exception e) {
            return ResultVoUtil.error(e.getMessage());
        }
        return resultVo;
    }

    /**
     * 撤回
     */
    @GetMapping("/revoke/{idArray}")
    @ResponseBody
    public ResultVo revoke(@PathVariable("idArray") String idArray) {
        ResultVo resultVo;
        try {
            resultVo = stockoutPreOrderInfoService.revoke(idArray);
        } catch (Exception e) {
            return ResultVoUtil.error(e.getMessage());
        }
        return resultVo;
    }

    /**
     * 确定出库
     */
    @GetMapping("/sureStock/{idArray}")
    @ResponseBody
    public ResultVo sureStock(@PathVariable("idArray") String idArray) {
        ResultVo resultVo;
        try {
            resultVo = stockoutPreOrderInfoService.sureStock(idArray);
        } catch (Exception e) {
            return ResultVoUtil.error(e.getMessage());
        }
        return resultVo;
    }
}