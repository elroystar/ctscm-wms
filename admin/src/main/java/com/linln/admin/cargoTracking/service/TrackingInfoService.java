package com.linln.admin.cargoTracking.service;

import com.linln.admin.cargoTracking.domain.TrackingInfo;
import com.linln.common.enums.StatusEnum;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 小懒虫
 * @date 2021/06/24
 */
public interface TrackingInfoService {

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<TrackingInfo> getPageList(Example<TrackingInfo> example);

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    TrackingInfo getById(Long id);

    /**
     * 保存数据
     * @param trackingInfo 实体对象
     */
    TrackingInfo save(TrackingInfo trackingInfo);

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);
}