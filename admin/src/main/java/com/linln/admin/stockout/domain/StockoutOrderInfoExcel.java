package com.linln.admin.stockout.domain;

import com.linln.component.excel.annotation.Excel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 小懒虫
 * @date 2020/12/11
 */
@Getter
@Setter
public class StockoutOrderInfoExcel implements Serializable {

    // 出库单号
    @Excel("出库单号")
    private String outNo;
    // 型号/物料号
    @Excel("型号/物料号")
    private String model;
    // 序列号/SN
    @Excel("序列号/SN")
    private String sn;
    // QTY
    @Excel("QTY")
    private Integer qty;
    // 入库 DN#
    @Excel("入库 DN#")
    private String dn;
    // PO#
    @Excel("PO#")
    private String po;
    // SO#
    @Excel("SO#")
    private String so;
    // 合同号/批次号
    @Excel("合同号/批次号")
    private String contractNo;
    // 生产日期
    @Excel("生产日期")
    private Date productDate;
    // 入库重量/KG
    @Excel("入库重量/KG")
    private BigDecimal weight;
    // 入库体积/CBM
    @Excel("入库体积/CBM")
    private BigDecimal volume;
    // 商品名称/物料描述
    @Excel("商品名称/物料描述")
    private String goodsName;
    // 产品分类
    @Excel("产品分类")
    private String productType;
    // 入库供应商
    @Excel("入库供应商")
    private String supplier;
    // 库位号
    @Excel("库位号")
    private String locationNo;
    // 内部号
    @Excel("内部号")
    private String internalNo;
    // 备注
    @Excel("备注")
    private String remark;

}