package com.linln.admin.stockout.service.impl;

import com.linln.admin.stockout.domain.StockoutOrder;
import com.linln.admin.stockout.repository.StockoutOrderRepository;
import com.linln.admin.stockout.service.StockoutOrderService;
import com.linln.common.data.PageSort;
import com.linln.common.enums.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 小懒虫
 * @date 2020/12/30
 */
@Service
public class StockoutOrderServiceImpl implements StockoutOrderService {

    @Autowired
    private StockoutOrderRepository stockoutOrderRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public StockoutOrder getById(Long id) {
        return stockoutOrderRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<StockoutOrder> getPageList(Example<StockoutOrder> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return stockoutOrderRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param stockoutOrder 实体对象
     */
    @Override
    public StockoutOrder save(StockoutOrder stockoutOrder) {
        return stockoutOrderRepository.save(stockoutOrder);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return stockoutOrderRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }

    @Override
    public Integer getCountNow() {
        return stockoutOrderRepository.getCountNow();
    }
}