package com.sqin.internalcommon.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    //
    private static final String SIGN = "CPFsqin!@#$$";

    private static final String JWT_KEY = "passengerPhone";


    //生成token
    public static String generateToken(String passengerPhone) {
        Map<String, String> map = new HashMap<>();
        map.put(JWT_KEY, passengerPhone);

        // token 過期時間
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Date date = calendar.getTime();

        JWTCreator.Builder builder = JWT.create();
        // 整合map
        map.forEach((k, v) -> {
            builder.withClaim(k, v);
        });
        // 整合過期時間
        builder.withExpiresAt(date);

        // 生成 token
        String sign = builder.sign(Algorithm.HMAC256(SIGN));
        return sign;
    }


    // 解析token
    public static String parseToken(String token) {
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
        return verify.getClaim(JWT_KEY).toString();
    }

    // 測試生成Token
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("name", "zhang san");
        map.put("age", "3");
        String s = generateToken("17717530050");
        System.out.println(s);

        System.out.println("解析后的:" + parseToken(s));
    }

}
