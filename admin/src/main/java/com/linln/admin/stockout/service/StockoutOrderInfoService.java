package com.linln.admin.stockout.service;

import com.linln.admin.stockout.domain.StockoutOrderInfo;
import com.linln.common.enums.StatusEnum;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 小懒虫
 * @date 2020/12/11
 */
public interface StockoutOrderInfoService {

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<StockoutOrderInfo> getPageList(Example<StockoutOrderInfo> example);

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    StockoutOrderInfo getById(Long id);

    /**
     * 保存数据
     * @param stockoutOrderInfo 实体对象
     */
    StockoutOrderInfo save(StockoutOrderInfo stockoutOrderInfo);

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);
}