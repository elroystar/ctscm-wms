package com.linln.admin.warehouse.domain;

import com.linln.component.excel.annotation.Excel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author star
 * @date 2020/11/18
 */
@Getter
@Setter
public class WarehouseLocationExcel implements Serializable {

    // 库位编码
    @Excel("库位编码")
    private String code;

    // 备注
    @Excel("备注")
    private String remark;
}