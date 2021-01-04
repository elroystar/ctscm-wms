package com.linln.admin.stock.repository;

import com.linln.admin.stock.domain.StockOrderInfo;
import com.linln.modules.system.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Query(value = "select * from wms_stock_order_info where model = :outValue ", nativeQuery = true)
    List<StockOrderInfo> getByModel(String outValue);

    @Query(value = "select * from wms_stock_order_info where sn = :outValue ", nativeQuery = true)
    List<StockOrderInfo> getBySn(String outValue);

    @Query(value = "select * from wms_stock_order_info where dn = :outValue ", nativeQuery = true)
    List<StockOrderInfo> getByDn(String outValue);

    @Query(value = "select * from wms_stock_order_info where po = :outValue ", nativeQuery = true)
    List<StockOrderInfo> getByPo(String outValue);

    @Query(value = "select * from wms_stock_order_info where so = :outValue ", nativeQuery = true)
    List<StockOrderInfo> getBySo(String outValue);

    @Query(value = "select * from wms_stock_order_info where contract_no = :outValue ", nativeQuery = true)
    List<StockOrderInfo> getByContractNo(String outValue);

    @Query(value = "select * from wms_stock_order_info where internal_no = :outValue ", nativeQuery = true)
    List<StockOrderInfo> getByInternalNo(String outValue);
}