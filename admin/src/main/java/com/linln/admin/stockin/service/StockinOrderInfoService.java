package com.linln.admin.stockin.service;

import com.linln.admin.stockin.domain.StockinOrderInfo;
import com.linln.common.enums.StatusEnum;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 小懒虫
 * @date 2020/12/11
 */
public interface StockinOrderInfoService {

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<StockinOrderInfo> getPageList(Example<StockinOrderInfo> example);

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    StockinOrderInfo getById(Long id);

    /**
     * 保存数据
     * @param stockinOrderInfo 实体对象
     */
    StockinOrderInfo save(StockinOrderInfo stockinOrderInfo);

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);

    @Transactional
    Boolean updateOrderInfoStatusById(String status, Long id);

    Integer checkSn(String sn);

    @Transactional
    void putInStorage(StockinOrderInfo byId);

    Integer checkNumByOrderNo(String orderNo);

    void deleteById(long id);
}