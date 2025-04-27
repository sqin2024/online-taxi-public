package com.sqin.internalcommon.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sqin.internalcommon.dto.TokenResult;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    //
    private static final String SIGN = "CPFsqin!@#$$";

    private static final String JWT_KEY_PHONE = "passengerPhone";

    private static final String JWT_KEY_IDENTITY = "identity";

    private static final String JWT_TOKEN_TYPE = "tokenType";


    //生成token
    public static String generateToken(String passengerPhone, String identity, String tokenType) {
        Map<String, String> map = new HashMap<>();
        map.put(JWT_KEY_PHONE, passengerPhone);
        map.put(JWT_KEY_IDENTITY, identity);
        map.put(JWT_TOKEN_TYPE, tokenType);

        // token 過期時間
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, 1);
//        Date date = calendar.getTime();

        JWTCreator.Builder builder = JWT.create();
        // 整合map
        map.forEach((k, v) -> {
            builder.withClaim(k, v);
        });
        // 整合過期時間
//        builder.withExpiresAt(date);

        // 生成 token
        String sign = builder.sign(Algorithm.HMAC256(SIGN));
        return sign;
    }


    // 解析token
    public static TokenResult parseToken(String token) {
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
        String phone = verify.getClaim(JWT_KEY_PHONE).asString();
        String identity = verify.getClaim(JWT_KEY_IDENTITY).asString();
        TokenResult tokenResult = new TokenResult();
        tokenResult.setIdentity(identity);
        tokenResult.setPhone(phone);
        return tokenResult;
    }

    // 測試生成Token
    public static void main(String[] args) {
        String s = generateToken("17717530050", "1", "access");
        System.out.println(s);

        System.out.println("解析后的:" + parseToken(s).getPhone() + "...身份：" + parseToken(s).getIdentity());
    }

}
