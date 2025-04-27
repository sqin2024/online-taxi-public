package com.sqin.internalcommon.util;

public class RedisPrefixUtils {

    private static String verificationCodePrefix = "passenger-verification-code-";

    private static String tokenPrefix = "token-";

    /**
     * 根据手机号，生成验证码的redis key
     * @param passengerPhone
     * @return
     */
    public static String generateKeyByPhone(String passengerPhone) {
        return verificationCodePrefix + passengerPhone;
    }

    /**
     * 根据手机号和身份标识，生成token
     * @param phone
     * @param identity
     * @return
     */
    public static String generateTokeyKey(String phone, String identity) {
        return tokenPrefix + phone + "-" + identity;
    }
}
