package com.linln.admin.stock.repository;

import com.linln.admin.stock.domain.StockOrderInfo;
import com.linln.modules.system.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 小懒虫
 * @date 2020/12/11
 */
public interface StockOrderInfoRepository extends BaseRepository<StockOrderInfo, Long> {

    @Query(value = "select count(1) from wms_stock_order_info where sn = :sn ", nativeQuery = true)
    Integer checkSn(String sn);

    @Transactional
    @Modifying
    @Query(value = "update #{#entityName} set qty = ?1  where id = ?2")
    Integer updateQtyById(Integer surQty, Long orderId);
}