package com.linln.admin.warehouse.validator;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author star
 * @date 2020/11/18
 */
@Data
public class WarehouseLocationValid implements Serializable {
    @NotEmpty(message = "库位编码不能为空")
    private String code;
    @NotNull(message = "库区表Id不能为空")
    private Long regionId;
}