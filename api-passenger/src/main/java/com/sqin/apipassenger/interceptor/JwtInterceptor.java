package com.sqin.apipassenger.interceptor;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.sqin.internalcommon.constant.TokenConstants;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.dto.TokenResult;
import com.sqin.internalcommon.util.JwtUtils;
import com.sqin.internalcommon.util.RedisPrefixUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * JwtInterceptor拦截器是在bean实例化之前初始化的，JwtInterceptor初始化后，stringRedisTemplate还没有初始化，所以这里直接注入StringRedisTemplate是成功不了的。
 * 解决方式：在拦截器初始化之前呢，给bean进行初始化，代码见InterceptorConfig.
 */
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        boolean result = true;
        String resultString = "";

        String token = request.getHeader("Authorization");
        TokenResult tokenResult = null;
        try {
            tokenResult = JwtUtils.parseToken(token);
        } catch (SignatureVerificationException e) {
            resultString = "token sign error";
            result = false;
        } catch (TokenExpiredException e) {
            resultString = "token time out";
            result = false;
        } catch (AlgorithmMismatchException e){
            resultString = "token AlgorithmMismatchException";
            result = false;
        } catch (Exception e) {
            resultString = "token invalid";
            result = false;
        }

        if(tokenResult == null) {
            resultString = "token invalid";
            result = false;
        } else {
            // get token from redis
            String phone = tokenResult.getPhone();
            String identity = tokenResult.getIdentity();
            String accessTokenKey = RedisPrefixUtils.generateTokeyKey(phone, identity, TokenConstants.ACCESS_TOKEN_TYPE);
            String refreshTokenKey = RedisPrefixUtils.generateTokeyKey(phone, identity, TokenConstants.REFRESH_TOKEN_TYPE);
            String cachedToken = stringRedisTemplate.opsForValue().get(accessTokenKey);
            if(StringUtils.isBlank(cachedToken)) {
                resultString = "no token in redis";
                result = false;
            } else {
                if(!token.trim().equals(cachedToken.trim())) {
                    resultString = "token from user is different with cached token";
                    result = false;
                }
            }
        }

        if(!result) {
            PrintWriter out = response.getWriter();
            out.println(JSONObject.fromObject(ResponseResult.fail(resultString)).toString());
        }
        return result;
    }
}
