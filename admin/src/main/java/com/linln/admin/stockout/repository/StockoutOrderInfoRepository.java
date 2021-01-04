package com.linln.admin.stockout.repository;

import com.linln.admin.stockout.domain.StockoutOrderInfo;
import com.linln.modules.system.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 小懒虫
 * @date 2020/12/11
 */
public interface StockoutOrderInfoRepository extends BaseRepository<StockoutOrderInfo, Long> {

    @Query(value = "select count(1) from wms_stockout_order_info where sn = :sn ", nativeQuery = true)
    Integer checkSn(String sn);

    @Query(value = "select * from wms_stockout_order_info where out_no = :orderNo ", nativeQuery = true)
    List<StockoutOrderInfo> getByOutNo(String orderNo);

    @Transactional
    @Modifying
    @Query(value = "update #{#entityName} set outNo = ?1, status = 1  where id = ?2")
    void updateOutNoById(String outNo, long orderId);
}