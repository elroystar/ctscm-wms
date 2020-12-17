package com.linln.admin.stockin.controller;

import com.linln.admin.stockin.domain.StockinOrder;
import com.linln.admin.stockin.service.StockinOrderService;
import com.linln.common.enums.OrderStatusEnum;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 小懒虫
 * @date 2020/12/15
 */
@Controller
@RequestMapping("/stockin/stockinAlreadyOrder")
public class StockinAlreadyOrderController {

    @Autowired
    private StockinOrderService stockinOrderService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("stockin:stockinAlreadyOrder:index")
    public String index(Model model, StockinOrder stockinAlreadyOrder) {
        stockinAlreadyOrder.setStatus(OrderStatusEnum.ALL.getCode());
        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("status", match -> match.startsWith());

        // 获取数据列表
        Example<StockinOrder> example = Example.of(stockinAlreadyOrder, matcher);
        Page<StockinOrder> list = stockinOrderService.getPageList(example);

        for (StockinOrder order : list.getContent()) {
            order.setStatus(OrderStatusEnum.getMessageByCode(order.getStatus()));
        }

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/stockin/stockinAlreadyOrder/index";
    }
}