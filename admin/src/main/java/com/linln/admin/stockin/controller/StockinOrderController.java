package com.linln.admin.stockin.controller;

import com.linln.admin.stockin.domain.StockinOrder;
import com.linln.admin.stockin.domain.StockinOrderInfo;
import com.linln.admin.stockin.service.StockinOrderInfoService;
import com.linln.admin.stockin.service.StockinOrderService;
import com.linln.admin.stockin.validator.StockinOrderValid;
import com.linln.common.enums.OrderStatusEnum;
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

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author 小懒虫
 * @date 2020/12/10
 */
@Controller
@RequestMapping("/stockin/stockinOrder")
public class StockinOrderController {

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private StockinOrderService stockinOrderService;

    @Autowired
    private StockinOrderInfoService stockinOrderInfoService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("stockin:stockinOrder:index")
    public String index(Model model, StockinOrder stockinOrder) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("status", match -> match.startsWith());

        // 获取数据列表
        Example<StockinOrder> example = Example.of(stockinOrder, matcher);
        Page<StockinOrder> list = stockinOrderService.getPageList(example);

        for (StockinOrder order : list.getContent()) {
            order.setStatus(OrderStatusEnum.getMessageByCode(order.getStatus()));
        }

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/stockin/stockinOrder/index";
    }

    /**
     * 跳转到添加明细页面
     */
    @GetMapping("/addInfo/{orderNo}")
    @RequiresPermissions("stockin:stockinOrderInfo:index")
    public String addInfo(@PathVariable("orderNo") String orderNo, Model model) {

        StockinOrderInfo stockinOrderInfo = new StockinOrderInfo();
        stockinOrderInfo.setOrderNo(orderNo);
        stockinOrderInfo.setStatus(null);

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();

        // 获取数据列表
        Example<StockinOrderInfo> example = Example.of(stockinOrderInfo, matcher);
        Page<StockinOrderInfo> list = stockinOrderInfoService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        model.addAttribute("orderNo", orderNo);

        return "/stockin/stockinOrderInfo/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("stockin:stockinOrder:add")
    public String toAdd() {
        return "/stockin/stockinOrder/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("stockin:stockinOrder:edit")
    public String toEdit(@PathVariable("id") StockinOrder stockinOrder, Model model) {
        model.addAttribute("stockinOrder", stockinOrder);
        return "/stockin/stockinOrder/add";
    }

    /**
     * 保存添加/修改的数据
     *
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"stockin:stockinOrder:add", "stockin:stockinOrder:edit"})
    @ResponseBody
    public ResultVo save(@Validated StockinOrderValid valid, StockinOrder stockinOrder) {
        // 复制保留无需修改的数据
        if (stockinOrder.getId() != null) {
            StockinOrder beStockinOrder = stockinOrderService.getById(stockinOrder.getId());
            EntityBeanUtil.copyProperties(beStockinOrder, stockinOrder);
        }
        synchronized (this) {
            String date = dateTimeFormatter.format(LocalDateTime.now());
            Integer countNow = stockinOrderService.getCountNow();
            Integer num = ++countNow;
            String format = String.format("%04d", num);
            String orderNo = MessageFormat.format("{0}{1}-{2}", "R", date, format);
            stockinOrder.setOrderNo(orderNo);
            // 保存数据
            stockinOrderService.save(stockinOrder);
        }
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("stockin:stockinOrder:detail")
    public String toDetail(@PathVariable("id") StockinOrder stockinOrder, Model model) {
        model.addAttribute("stockinOrder", stockinOrder);
        return "/stockin/stockinOrder/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("stockin:stockinOrder:status")
    @ResponseBody
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (stockinOrderService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }
}