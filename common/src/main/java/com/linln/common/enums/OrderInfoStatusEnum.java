package com.linln.common.enums;

import lombok.Getter;

/**
 * 数据状态枚举-用于逻辑删除控制
 * @author 小懒虫
 * @date 2018/8/14
 */
@Getter
public enum OrderInfoStatusEnum {

    /**
     * 未过检
     */
    UNCHECKED("unchecked", "未过检"),
    /**
     * 已过检
     */
    CHECKED("checked", "已过检"),
    /**
     * 待复检
     */
    RECHECKED("rechecked", "待复检");

    private String code;

    private String message;

    OrderInfoStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessageByCode(String code) {
        for (OrderInfoStatusEnum statusEnum : OrderInfoStatusEnum.values()) {
            if (statusEnum.code.equals(code)) {
                return statusEnum.message;
            }
        }
        return OrderInfoStatusEnum.UNCHECKED.message;
    }
}

