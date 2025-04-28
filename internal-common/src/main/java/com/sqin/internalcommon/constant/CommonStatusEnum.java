package com.sqin.internalcommon.constant;

import lombok.Getter;

public enum CommonStatusEnum {

    VERIFICATION_CODE_ERROR(1099, "驗證碼不正確"),

    TOKEN_ERROR(1199, "Token错误"),

    USER_NOT_EXIST(1200, "user不存在"),

    PRICE_RULE_EMPTY(1300, "计价规则不存在 "),

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
