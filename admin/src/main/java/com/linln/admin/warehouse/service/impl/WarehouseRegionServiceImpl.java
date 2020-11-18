package com.linln.admin.warehouse.service.impl;

import com.linln.admin.warehouse.domain.WarehouseRegion;
import com.linln.admin.warehouse.repository.WarehouseRegionRepository;
import com.linln.admin.warehouse.service.WarehouseRegionService;
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
public class WarehouseRegionServiceImpl implements WarehouseRegionService {

    @Autowired
    private WarehouseRegionRepository warehouseRegionRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public WarehouseRegion getById(Long id) {
        return warehouseRegionRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<WarehouseRegion> getPageList(Example<WarehouseRegion> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return warehouseRegionRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param warehouseRegion 实体对象
     */
    @Override
    public WarehouseRegion save(WarehouseRegion warehouseRegion) {
        return warehouseRegionRepository.save(warehouseRegion);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return warehouseRegionRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }
}