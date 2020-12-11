package com.linln.admin.stockin.domain;

import com.linln.component.excel.annotation.Excel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 小懒虫
 * @date 2020/12/11
 */
@Getter
@Setter
public class StockinOrderInfoExcel implements Serializable {

    // 型号/物料号
    @Excel("型号/物料号")
    private String model;

    // 商品名称/物料描述
    @Excel("商品名称/物料描述")
    private String goodsName;

    // 产品分类
    @Excel("产品分类")
    private String productType;

    // 序列号/SN
    @Excel("序列号/SN")
    private String sn;

    // 生产日期
    @Excel("生产日期")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date productDate;

    // QTY
    @Excel("QTY")
    private Integer qty;

    // 库位号
    @Excel("库位号")
    private String locationNo;

    // 入库 DN#
    @Excel("入库DN#")
    private String dn;

    // 合同号/批次号
    @Excel("合同号/批次号")
    private String contractNo;

    // PO#
    @Excel("PO#")
    private String po;

    // SO#
    @Excel("SO#")
    private String so;

    // 内部号
    @Excel("内部号")
    private String internalNo;

    // 备注
    @Excel("备注")
    private String remark;

    // 入库重量/KG
    @Excel("入库重量/KG")
    private BigDecimal weight;

    // 入库体积/CBM
    @Excel("入库体积/CBM")
    private BigDecimal volume;

    // 入库供应商
    @Excel("入库供应商")
    private String supplier;

}