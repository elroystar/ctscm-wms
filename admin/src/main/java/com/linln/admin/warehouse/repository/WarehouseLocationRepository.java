package com.linln.admin.warehouse.repository;

import com.linln.admin.warehouse.domain.WarehouseLocation;
import com.linln.modules.system.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author star
 * @date 2020/11/18
 */
public interface WarehouseLocationRepository extends BaseRepository<WarehouseLocation, Long> {

    @Query(value = "select l.* from wms_warehouse_location l where l.region_id = :regionId and l.code = :code ", nativeQuery = true)
    WarehouseLocation checkCodeByRegionId(Long regionId, String code);
}