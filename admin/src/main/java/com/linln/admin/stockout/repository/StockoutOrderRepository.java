package com.linln.admin.stockout.repository;

import com.linln.admin.stockout.domain.StockoutOrder;
import com.linln.modules.system.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author 小懒虫
 * @date 2020/12/30
 */
public interface StockoutOrderRepository extends BaseRepository<StockoutOrder, Long> {

    @Query(value = "select count(1) from wms_stockout_order where TO_DAYS(create_date) = TO_DAYS(NOW()) ", nativeQuery = true)
    Integer getCountNow();

}