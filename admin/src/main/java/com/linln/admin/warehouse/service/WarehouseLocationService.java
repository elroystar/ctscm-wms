package com.linln.admin.warehouse.service;

import com.linln.admin.warehouse.domain.WarehouseLocation;
import com.linln.common.enums.StatusEnum;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author star
 * @date 2020/11/18
 */
public interface WarehouseLocationService {

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<WarehouseLocation> getPageList(Example<WarehouseLocation> example);

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    WarehouseLocation getById(Long id);

    /**
     * 根据库区ID查询数据
     * @param regionId 库区主键ID
     */
    WarehouseLocation checkCodeByRegionId(Long regionId, String code);

    /**
     * 保存数据
     * @param warehouseLocation 实体对象
     */
    WarehouseLocation save(WarehouseLocation warehouseLocation);

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);
}