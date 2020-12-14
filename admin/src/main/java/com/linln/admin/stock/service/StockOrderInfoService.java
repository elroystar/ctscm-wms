package com.linln.admin.stock.service;

import com.linln.admin.stock.domain.StockOrderInfo;
import com.linln.common.enums.StatusEnum;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 小懒虫
 * @date 2020/12/11
 */
public interface StockOrderInfoService {

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<StockOrderInfo> getPageList(Example<StockOrderInfo> example);

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    StockOrderInfo getById(Long id);

    /**
     * 保存数据
     * @param stockOrderInfo 实体对象
     */
    StockOrderInfo save(StockOrderInfo stockOrderInfo);

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);

    Integer checkSn(String sn);
}