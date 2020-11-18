package com.linln.admin.warehouse.validator;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;

/**
 * @author star
 * @date 2020/11/18
 */
@Data
public class WarehouseRegionValid implements Serializable {
    @NotEmpty(message = "库区名称不能为空")
    private String name;
}