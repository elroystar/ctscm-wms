package com.linln.common.enums;

import lombok.Getter;

/**
 * 数据状态枚举-用于逻辑删除控制
 * @author 小懒虫
 * @date 2018/8/14
 */
@Getter
public enum OrderStatusEnum {

    /**
     * 预入
     */
    PRE("pre", "预入"),
    /**
     * 全入
     */
    ALL("final_all", "全入"),
    /**
     * 部分入
     */
    PART("pre_part", "部分入");

    private String code;

    private String message;

    OrderStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessageByCode(String code) {
        for (OrderStatusEnum statusEnum : OrderStatusEnum.values()) {
            if (statusEnum.code.equals(code)) {
                return statusEnum.message;
            }
        }
        return OrderStatusEnum.PRE.message;
    }
}

