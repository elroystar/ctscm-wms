package com.linln.admin.cargoTracking.service.impl;

import com.linln.admin.cargoTracking.domain.TrackingInfo;
import com.linln.admin.cargoTracking.repository.TrackingInfoRepository;
import com.linln.admin.cargoTracking.service.TrackingInfoService;
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
 * @date 2021/06/24
 */
@Service
public class TrackingInfoServiceImpl implements TrackingInfoService {

    @Autowired
    private TrackingInfoRepository trackingInfoRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public TrackingInfo getById(Long id) {
        return trackingInfoRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<TrackingInfo> getPageList(Example<TrackingInfo> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return trackingInfoRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param trackingInfo 实体对象
     */
    @Override
    public TrackingInfo save(TrackingInfo trackingInfo) {
        return trackingInfoRepository.save(trackingInfo);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return trackingInfoRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }
}