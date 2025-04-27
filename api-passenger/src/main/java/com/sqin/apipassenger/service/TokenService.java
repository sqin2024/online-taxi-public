package com.sqin.apipassenger.service;

import com.sqin.internalcommon.constant.CommonStatusEnum;
import com.sqin.internalcommon.constant.TokenConstants;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.dto.TokenResult;
import com.sqin.internalcommon.response.TokenResponse;
import com.sqin.internalcommon.util.JwtUtils;
import com.sqin.internalcommon.util.RedisPrefixUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public ResponseResult refreshToken(String refreshTokenSrc) {

        // 解析refreshToken
        TokenResult tokenResult = JwtUtils.checkToken(refreshTokenSrc);

        if( tokenResult == null) {
            return ResponseResult.fail(CommonStatusEnum.TOKEN_ERROR.getCode(), CommonStatusEnum.TOKEN_ERROR.getValue());
        }

        // 读取redis中的refreshToken

        String phone = tokenResult.getPhone();
        String identity = tokenResult.getIdentity();
        String refreshTokenKey = RedisPrefixUtils.generateTokeyKey(phone, identity, TokenConstants.REFRESH_TOKEN_TYPE);

        // 校验token
        String refreshTokenRedis = stringRedisTemplate.opsForValue().get(refreshTokenKey);
        if(StringUtils.isBlank(refreshTokenRedis) || !refreshTokenSrc.trim().equals(refreshTokenRedis.trim())) {
            return ResponseResult.fail(CommonStatusEnum.TOKEN_ERROR.getCode(), CommonStatusEnum.TOKEN_ERROR.getValue());
        }

        // 生成双token
        String refreshToken = JwtUtils.generateToken(phone, identity, TokenConstants.REFRESH_TOKEN_TYPE);
        String accessToken = JwtUtils.generateToken(phone, identity, TokenConstants.ACCESS_TOKEN_TYPE);

        String accessTokenKey = RedisPrefixUtils.generateTokeyKey(phone, identity, TokenConstants.ACCESS_TOKEN_TYPE);

        // 这里增加了有限效后，就不需要给token本身设置有效期了
        stringRedisTemplate.opsForValue().set(accessTokenKey, accessToken, 30, TimeUnit.DAYS);
        // 这里refreshToken比accessToken的有效期要长一点点
        stringRedisTemplate.opsForValue().set(refreshTokenKey, refreshToken, 31, TimeUnit.DAYS);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setRefreshToken(refreshToken);
        tokenResponse.setAccessToken(accessToken);

        return ResponseResult.success(tokenResponse);
    }

}
