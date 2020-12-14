package com.linln.admin.stock.repository;

import com.linln.admin.stock.domain.StockOrderInfo;
import com.linln.modules.system.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author 小懒虫
 * @date 2020/12/11
 */
public interface StockOrderInfoRepository extends BaseRepository<StockOrderInfo, Long> {

    @Query(value = "select count(1) from wms_stock_order_info where sn = :sn ", nativeQuery = true)
    Integer checkSn(String sn);
}