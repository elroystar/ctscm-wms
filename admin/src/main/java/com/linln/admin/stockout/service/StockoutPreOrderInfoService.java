package com.linln.admin.stockout.service;

import com.linln.admin.stock.domain.StockOrderInfo;
import com.linln.common.enums.StatusEnum;
import com.linln.common.vo.ResultVo;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 小懒虫
 * @date 2020/12/25
 */
public interface StockoutPreOrderInfoService {

    /**
     * 获取分页列表数据
     *
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<StockOrderInfo> getPageList(Example<StockOrderInfo> example);

    /**
     * 根据ID查询数据
     *
     * @param id 主键ID
     */
    StockOrderInfo getById(Long id);

    /**
     * 保存数据
     *
     * @param stockoutPreOrderInfo 实体对象
     */
    StockOrderInfo save(StockOrderInfo stockoutPreOrderInfo);

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);

    @Transactional
    ResultVo outQtyInput(String outType, String outValue, String outQtyInput);

    @Transactional
    ResultVo outQtyTd(String outQtyTdArray);

    ResultVo revoke(String idArray);

    ResultVo sureStock(String idArray);
}