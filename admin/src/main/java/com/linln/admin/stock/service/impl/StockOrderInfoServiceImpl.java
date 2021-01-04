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
     *
     * @param id 主键ID
     */
    @Override
    @Transactional
    public StockOrderInfo getById(Long id) {
        return stockOrderInfoRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     *
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
     *
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

    @Override
    public Integer checkSn(String sn) {
        return stockOrderInfoRepository.checkSn(sn);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        stockOrderInfoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateQtyById(Integer surQty, Long orderId) {
        stockOrderInfoRepository.updateQtyById(surQty, orderId);
    }

    @Override
    public List<StockOrderInfo> getByModel(String outValue) {
        return stockOrderInfoRepository.getByModel(outValue);
    }

    @Override
    public List<StockOrderInfo> getBySn(String outValue) {
        return stockOrderInfoRepository.getBySn(outValue);
    }

    @Override
    public List<StockOrderInfo> getByDn(String outValue) {
        return stockOrderInfoRepository.getByDn(outValue);
    }

    @Override
    public List<StockOrderInfo> getByPo(String outValue) {
        return stockOrderInfoRepository.getByPo(outValue);
    }

    @Override
    public List<StockOrderInfo> getBySo(String outValue) {
        return stockOrderInfoRepository.getBySo(outValue);
    }

    @Override
    public List<StockOrderInfo> getByContractNo(String outValue) {
        return stockOrderInfoRepository.getByContractNo(outValue);
    }

    @Override
    public List<StockOrderInfo> getByInternalNo(String outValue) {
        return stockOrderInfoRepository.getByInternalNo(outValue);
    }

}