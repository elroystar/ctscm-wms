package com.linln.admin.stockout.service.impl;

import com.linln.admin.stockout.domain.StockoutOrderInfo;
import com.linln.admin.stockout.repository.StockoutOrderInfoRepository;
import com.linln.admin.stockout.service.StockoutOrderInfoService;
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
public class StockoutOrderInfoServiceImpl implements StockoutOrderInfoService {

    @Autowired
    private StockoutOrderInfoRepository stockoutOrderInfoRepository;

    /**
     * 根据ID查询数据
     *
     * @param id 主键ID
     */
    @Override
    @Transactional
    public StockoutOrderInfo getById(Long id) {
        return stockoutOrderInfoRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     *
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<StockoutOrderInfo> getPageList(Example<StockoutOrderInfo> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return stockoutOrderInfoRepository.findAll(example, page);
    }

    /**
     * 保存数据
     *
     * @param stockoutOrderInfo 实体对象
     */
    @Override
    @Transactional
    public StockoutOrderInfo save(StockoutOrderInfo stockoutOrderInfo) {
        return stockoutOrderInfoRepository.save(stockoutOrderInfo);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return stockoutOrderInfoRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }

    @Override
    public Integer checkSn(String sn) {
        return stockoutOrderInfoRepository.checkSn(sn);
    }

    @Override
    public void deleteById(Long id) {
        stockoutOrderInfoRepository.deleteById(id);
    }

    @Override
    public List<StockoutOrderInfo> getByOutNo(String orderNo) {
        return stockoutOrderInfoRepository.getByOutNo(orderNo);
    }

    @Override
    public void updateOutNoById(String outNo, long parseLong) {
        stockoutOrderInfoRepository.updateOutNoById(outNo, parseLong);
    }
}