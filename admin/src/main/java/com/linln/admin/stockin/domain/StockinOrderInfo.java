package com.linln.admin.stockin.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linln.common.enums.OrderInfoStatusEnum;
import com.linln.common.utils.StatusUtil;
import com.linln.modules.system.domain.User;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 小懒虫
 * @date 2020/12/11
 */
@Data
@Entity
@Table(name = "wms_stockin_order_info")
@EntityListeners(AuditingEntityListener.class)
@Where(clause = StatusUtil.NOT_DELETE)
public class StockinOrderInfo implements Serializable {
    // 主键ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 入库单号
    private String orderNo;
    // 库区id
    private String regionId;
    // 库位id
    private String locationId;
    // 型号/物料号
    private String model;
    // 序列号/SN
    private String sn;
    // QTY
    private Integer qty;
    // 入库 DN#
    private String dn;
    // PO#
    private String po;
    // SO#
    private String so;
    // 合同号/批次号
    private String contractNo;
    // 生产日期
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date productDate;
    // 入库重量/KG
    private BigDecimal weight;
    // 入库体积/CBM
    private BigDecimal volume;
    // 商品名称/物料描述
    private String goodsName;
    // 产品分类
    private String productType;
    // 入库供应商
    private String supplier;
    // 库位号
    private String locationNo;
    // 内部号
    private String internalNo;
    // 备注
    private String remark;
    // 创建时间
    @CreatedDate
    private Date createDate;
    // 更新时间
    @LastModifiedDate
    private Date updateDate;
    // 创建者
    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "create_by")
    @JsonIgnore
    private User createBy;
    // 更新者
    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "update_by")
    @JsonIgnore
    private User updateBy;
    // 数据状态
    private String status = OrderInfoStatusEnum.UNCHECKED.getMessage();
}