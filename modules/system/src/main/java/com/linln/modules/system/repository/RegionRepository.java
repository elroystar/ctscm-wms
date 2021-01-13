package com.linln.modules.system.repository;

import com.linln.modules.system.domain.Region;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author star
 * @date 2020/11/18
 */
public interface RegionRepository extends BaseRepository<Region, Long> {

    @Query(value = "select * from wms_user_region l where user_id = :userId order by create_date desc ", nativeQuery = true)
    List<Region> getRegionByUserId(String userId);

    @Transactional
    @Modifying
    @Query(value = "delete from #{#entityName} where user_id = ?1")
    void deleteByUserId(String userId);
}