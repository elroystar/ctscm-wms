package com.linln.admin.stockout.repository;

import com.linln.admin.stockout.domain.StockoutOrderInfo;
import com.linln.modules.system.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author 小懒虫
 * @date 2020/12/11
 */
public interface StockoutOrderInfoRepository extends BaseRepository<StockoutOrderInfo, Long> {

    @Query(value = "select count(1) from wms_stockout_order_info where sn = :sn ", nativeQuery = true)
    Integer checkSn(String sn);
}