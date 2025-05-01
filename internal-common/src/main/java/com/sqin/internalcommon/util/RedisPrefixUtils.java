package com.sqin.internalcommon.util;

public class RedisPrefixUtils {

    private static String passengerVerificationCodePrefix = "passenger-verification-code-";

    private static String driverVerificationCodePrefix = "driver-verification-code-";

    private static String tokenPrefix = "token-";

    /**
     * 根据手机号，生成验证码的redis key
     * @param passengerPhone
     * @return
     */
    public static String generatePassengerKeyByPhone(String passengerPhone) {
        return passengerVerificationCodePrefix + passengerPhone;
    }

    /**
     * 根据手机号，生成验证码的redis key
     * @param driverPhone
     * @return
     */
    public static String generateDriverKeyByPhone(String driverPhone) {
        return driverVerificationCodePrefix + driverPhone;
    }

    /**
     * 根据手机号和身份标识，生成token
     * @param phone
     * @param identity
     * @return
     */
    public static String generateTokeyKey(String phone, String identity, String tokenType) {
        return tokenPrefix + phone + "-" + identity + "-" + tokenType;
    }
}
