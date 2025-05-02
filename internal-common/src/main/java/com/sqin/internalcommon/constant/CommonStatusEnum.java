package com.sqin.internalcommon.constant;

import lombok.Getter;

public enum CommonStatusEnum {

    VERIFICATION_CODE_ERROR(1099, "驗證碼不正確"),

    TOKEN_ERROR(1199, "Token错误"),

    USER_NOT_EXIST(1200, "user不存在"),

    PRICE_RULE_EMPTY(1300, "计价规则不存在 "),

    PRICE_RULE_EXISTS(1301, "计价规则存在, 不允许添加"),

    PRICE_RULE_NOT_EDIT(1302, "计价规则没有变化"),

    MAP_DISTRICT_ERROR(1400, "请求地区信息错误"),

    DRIVER_CAR_BIND_NOT_EXIST(1501, "司机汽车绑定关系不存在"),

    DRIVER_CAR_BIND_EXIST(1502, "司机汽车绑定关系已存在"),

    DRIVER_BIND_EXIST(1503, "司机已经绑定其他车辆"),

    CAR_BIND_EXIST(1504, "汽车已经被其他司机绑定"),

    DRIVER_NOT_EXIST(1600, "司机不存在"),



    SUCCESS(1, "success"),
    FAIL(0, "fail");

    @Getter
    private int code;

    @Getter
    private String value;

    CommonStatusEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
