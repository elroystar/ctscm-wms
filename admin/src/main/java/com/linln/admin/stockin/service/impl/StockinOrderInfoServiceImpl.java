package com.linln.admin.stockin.service.impl;

import com.linln.admin.stockin.domain.StockinOrderInfo;
import com.linln.admin.stockin.repository.StockinOrderInfoRepository;
import com.linln.admin.stockin.service.StockinOrderInfoService;
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
public class StockinOrderInfoServiceImpl implements StockinOrderInfoService {

    @Autowired
    private StockinOrderInfoRepository stockinOrderInfoRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public StockinOrderInfo getById(Long id) {
        return stockinOrderInfoRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<StockinOrderInfo> getPageList(Example<StockinOrderInfo> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return stockinOrderInfoRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param stockinOrderInfo 实体对象
     */
    @Override
    public StockinOrderInfo save(StockinOrderInfo stockinOrderInfo) {
        return stockinOrderInfoRepository.save(stockinOrderInfo);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return stockinOrderInfoRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }
}