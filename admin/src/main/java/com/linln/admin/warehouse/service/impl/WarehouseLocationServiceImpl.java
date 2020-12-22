package com.linln.admin.warehouse.service.impl;

import com.linln.admin.warehouse.domain.WarehouseLocation;
import com.linln.admin.warehouse.repository.WarehouseLocationRepository;
import com.linln.admin.warehouse.service.WarehouseLocationService;
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
 * @author star
 * @date 2020/11/18
 */
@Service
public class WarehouseLocationServiceImpl implements WarehouseLocationService {

    @Autowired
    private WarehouseLocationRepository warehouseLocationRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public WarehouseLocation getById(Long id) {
        return warehouseLocationRepository.findById(id).orElse(null);
    }

    @Override
    public WarehouseLocation checkCodeByRegionId(Long regionId, String code) {
        return warehouseLocationRepository.checkCodeByRegionId(regionId, code);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<WarehouseLocation> getPageList(Example<WarehouseLocation> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return warehouseLocationRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param warehouseLocation 实体对象
     */
    @Override
    public WarehouseLocation save(WarehouseLocation warehouseLocation) {
        return warehouseLocationRepository.save(warehouseLocation);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return warehouseLocationRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }

    @Override
    public List<WarehouseLocation> findAll() {
        return warehouseLocationRepository.findAll();
    }
}