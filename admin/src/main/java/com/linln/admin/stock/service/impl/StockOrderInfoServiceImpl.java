package com.linln.admin.stock.service.impl;

import com.linln.admin.stock.domain.StockOrderInfo;
import com.linln.admin.stock.repository.StockOrderInfoRepository;
import com.linln.admin.stock.service.StockOrderInfoService;
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
 * @date 2020/12/11
 */
@Service
public class StockOrderInfoServiceImpl implements StockOrderInfoService {

    @Autowired
    private StockOrderInfoRepository stockOrderInfoRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public StockOrderInfo getById(Long id) {
        return stockOrderInfoRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<StockOrderInfo> getPageList(Example<StockOrderInfo> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return stockOrderInfoRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param stockOrderInfo 实体对象
     */
    @Override
    public StockOrderInfo save(StockOrderInfo stockOrderInfo) {
        return stockOrderInfoRepository.save(stockOrderInfo);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return stockOrderInfoRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }
}