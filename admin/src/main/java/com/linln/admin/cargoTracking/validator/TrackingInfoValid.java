package com.linln.admin.cargoTracking.validator;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;

/**
 * @author 小懒虫
 * @date 2021/06/24
 */
@Data
public class TrackingInfoValid implements Serializable {
    @NotEmpty(message = "单号不能为空")
    private String orderNo;
}