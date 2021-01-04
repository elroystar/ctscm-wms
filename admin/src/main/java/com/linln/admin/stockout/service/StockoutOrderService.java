package com.linln.admin.stockout.service;

import com.linln.admin.stockout.domain.StockoutOrder;
import com.linln.common.enums.StatusEnum;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 小懒虫
 * @date 2020/12/30
 */
public interface StockoutOrderService {

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<StockoutOrder> getPageList(Example<StockoutOrder> example);

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    StockoutOrder getById(Long id);

    /**
     * 保存数据
     * @param stockoutOrder 实体对象
     */
    StockoutOrder save(StockoutOrder stockoutOrder);

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);

    Integer getCountNow();
}