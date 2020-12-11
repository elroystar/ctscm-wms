package com.linln.admin.stockin.service.impl;

import com.linln.admin.stockin.domain.StockinOrder;
import com.linln.admin.stockin.repository.StockinOrderRepository;
import com.linln.admin.stockin.service.StockinOrderService;
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
 * @date 2020/12/10
 */
@Service
public class StockinOrderServiceImpl implements StockinOrderService {

    @Autowired
    private StockinOrderRepository stockinOrderRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public StockinOrder getById(Long id) {
        return stockinOrderRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<StockinOrder> getPageList(Example<StockinOrder> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return stockinOrderRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param stockinOrder 实体对象
     */
    @Override
    public StockinOrder save(StockinOrder stockinOrder) {
        return stockinOrderRepository.save(stockinOrder);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return stockinOrderRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }

    @Override
    public Integer getCountNow() {
        return stockinOrderRepository.getCountNow();
    }
}