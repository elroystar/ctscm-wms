package com.linln.admin.stock.controller;

import com.linln.admin.stock.domain.StockOrderInfo;
import com.linln.admin.stock.service.StockOrderInfoService;
import com.linln.admin.stock.validator.StockOrderInfoValid;
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
@RequestMapping("/stock/stockOrderInfo")
public class StockOrderInfoController {

    @Autowired
    private StockOrderInfoService stockOrderInfoService;

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
        model.addAttribute("stockOrderInfo",stockOrderInfo);
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
}