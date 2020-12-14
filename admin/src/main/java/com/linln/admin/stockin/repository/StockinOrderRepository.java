package com.linln.admin.stockin.repository;

import com.linln.admin.stockin.domain.StockinOrder;
import com.linln.modules.system.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * @author 小懒虫
 * @date 2020/12/10
 */
public interface StockinOrderRepository extends BaseRepository<StockinOrder, Long> {

    @Query(value = "select count(1) from wms_stockin_order where TO_DAYS(create_date) = TO_DAYS(NOW()) ", nativeQuery = true)
    Integer getCountNow();

    @Transactional
    @Modifying
    @Query(value = "update #{#entityName} set status = ?1  where orderNo = ?2")
    void updateStatusByOrderNo(String code, String orderNo);
}