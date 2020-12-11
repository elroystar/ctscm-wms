package com.linln.admin.stockout.controller;

import com.linln.admin.stockout.domain.StockoutOrderInfo;
import com.linln.admin.stockout.service.StockoutOrderInfoService;
import com.linln.admin.stockout.validator.StockoutOrderInfoValid;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
 * @date 2020/12/11
 */
@Controller
@RequestMapping("/stockout/stockoutOrderInfo")
public class StockoutOrderInfoController {

    @Autowired
    private StockoutOrderInfoService stockoutOrderInfoService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("stockout:stockoutOrderInfo:index")
    public String index(Model model, StockoutOrderInfo stockoutOrderInfo) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();

        // 获取数据列表
        Example<StockoutOrderInfo> example = Example.of(stockoutOrderInfo, matcher);
        Page<StockoutOrderInfo> list = stockoutOrderInfoService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/stockout/stockoutOrderInfo/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("stockout:stockoutOrderInfo:add")
    public String toAdd() {
        return "/stockout/stockoutOrderInfo/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("stockout:stockoutOrderInfo:edit")
    public String toEdit(@PathVariable("id") StockoutOrderInfo stockoutOrderInfo, Model model) {
        model.addAttribute("stockoutOrderInfo", stockoutOrderInfo);
        return "/stockout/stockoutOrderInfo/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"stockout:stockoutOrderInfo:add", "stockout:stockoutOrderInfo:edit"})
    @ResponseBody
    public ResultVo save(@Validated StockoutOrderInfoValid valid, StockoutOrderInfo stockoutOrderInfo) {
        // 复制保留无需修改的数据
        if (stockoutOrderInfo.getId() != null) {
            StockoutOrderInfo beStockoutOrderInfo = stockoutOrderInfoService.getById(stockoutOrderInfo.getId());
            EntityBeanUtil.copyProperties(beStockoutOrderInfo, stockoutOrderInfo);
        }

        // 保存数据
        stockoutOrderInfoService.save(stockoutOrderInfo);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("stockout:stockoutOrderInfo:detail")
    public String toDetail(@PathVariable("id") StockoutOrderInfo stockoutOrderInfo, Model model) {
        model.addAttribute("stockoutOrderInfo",stockoutOrderInfo);
        return "/stockout/stockoutOrderInfo/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("stockout:stockoutOrderInfo:status")
    @ResponseBody
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (stockoutOrderInfoService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }
}