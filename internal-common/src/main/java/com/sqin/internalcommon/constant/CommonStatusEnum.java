package com.sqin.internalcommon.constant;

import lombok.Getter;

public enum CommonStatusEnum {

    VERIFICATION_CODE_ERROR(1099, "驗證碼不正確"),

    TOKEN_ERROR(1199, "Token错误"),

    USER_NOT_EXIST(1200, "user不存在"),

    PRICE_RULE_EMPTY(1300, "计价规则不存在 "),

    PRICE_RULE_EXISTS(1301, "计价规则存在, 不允许添加"),

    PRICE_RULE_NOT_EDIT(1302, "计价规则没有变化"),

    PRICE_RULE_CHANGED(1303, "计价规则已经发生变化"),

    MAP_DISTRICT_ERROR(1400, "请求地区信息错误"),

    DRIVER_CAR_BIND_NOT_EXIST(1501, "司机汽车绑定关系不存在"),

    DRIVER_CAR_BIND_EXIST(1502, "司机汽车绑定关系已存在"),

    DRIVER_BIND_EXIST(1503, "司机已经绑定其他车辆"),

    CAR_BIND_EXIST(1504, "汽车已经被其他司机绑定"),

    CITY_DRIVER_EMPTY(1505, "当前城市没有可用司机"),

    DRIVER_NOT_EXIST(1600, "司机不存在"),

    ORDER_GOING_ON(1700, "有正在进行的订单"),

    CITY_SERVICE_NOT_SERVICE(1701, "当前城市不提供服务"),

    AVAILABLE_DRIVER_EMPTY(1506, "可用司机为空"),
    /**
     * 验证码错误提示：1000-1099
     */
    CALL_USER_ADD_ERROR(1000,"调用新增用户异常"),

    CHECK_CODE_ERROR(1001,"验证手机号和验证码 异常"),

    /**
     * 用户提示：1200-1299
     */
    USER_NOT_EXISTS(1200,"当前用户不存在"),

    /**
     * 司机和车辆：1500-1599
     */
    DRIVER_CAR_BIND_NOT_EXISTS(1500,"司机和车辆绑定关系不存在"),

    DRIVER_NOT_EXITST(1501,"司机不存在"),

    DRIVER_CAR_BIND_EXISTS(1502,"司机和车辆绑定关系已存在，请勿重复绑定"),

    DRIVER_BIND_EXISTS(1503,"司机已经被绑定了，请勿重复绑定"),

    CAR_BIND_EXISTS(1504,"车辆已经被绑定了，请勿重复绑定"),

    CAR_NOT_EXISTS(1507,"车辆不存在"),

    DRIVER_STATUS_UPDATE_ERROR(1508,"司机工作状态修改失败"),

    /**
     * 下单异常
     */
    DEVICE_IS_BLACK(1601,"该设备超过下单次数"),

    ORDER_CANCEL_ERROR(1603, "订单取消失败"),

    ORDER_NOT_EXISTS(1604,"订单不存在"),

    ORDER_CAN_NOT_GRAB(1605 , "订单不能被抢"),

    ORDER_GRABING(1606,"订单正在被抢"),

    ORDER_UPDATE_ERROR(1607,"订单修改失败"),

    /**
     * 统一验证提示 1700-1799
     */
    VALIDATION_EXCEPTION(1700,"统一验证框架的错误提示"),

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
