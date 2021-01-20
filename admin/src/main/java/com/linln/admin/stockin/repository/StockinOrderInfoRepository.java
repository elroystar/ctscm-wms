package com.linln.admin.stockin.repository;

import com.linln.admin.stockin.domain.StockinOrderInfo;
import com.linln.modules.system.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 小懒虫
 * @date 2020/12/11
 */
public interface StockinOrderInfoRepository extends BaseRepository<StockinOrderInfo, Long>, JpaSpecificationExecutor<StockinOrderInfo> {

    /**
     * 批量更新数据状态
     * #{#entityName} 实体类对象
     *
     * @param status 状态
     * @param id     ID列表
     * @return 更新数量
     */
    @Transactional
    @Modifying
    @Query(value = "update #{#entityName} set status = ?1  where id = ?2")
    Integer updateOrderInfoStatusById(String status, long id);

    @Query(value = "select count(1) from wms_stockin_order_info where sn = :sn ", nativeQuery = true)
    Integer checkSn(String sn);

    @Query(value = "select count(1) from wms_stockin_order_info where order_no = :orderNo and status != 'warehousing'", nativeQuery = true)
    Integer checkNumByOrderNo(String orderNo);
}