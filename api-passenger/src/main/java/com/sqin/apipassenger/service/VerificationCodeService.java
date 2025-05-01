package com.sqin.apipassenger.service;

import com.sqin.apipassenger.remote.ServicePassengerUserClient;
import com.sqin.apipassenger.remote.ServiceVerificationCodeClient;
import com.sqin.internalcommon.constant.CommonStatusEnum;
import com.sqin.internalcommon.constant.IdentityConstant;
import com.sqin.internalcommon.constant.TokenConstants;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.VerificationCodeDTO;
import com.sqin.internalcommon.response.NumberCodeResponse;
import com.sqin.internalcommon.response.TokenResponse;
import com.sqin.internalcommon.util.JwtUtils;
import com.sqin.internalcommon.util.RedisPrefixUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ServiceVerificationCodeClient serviceVerificationCodeClient;

    @Autowired
    private ServicePassengerUserClient servicePassengerUserClient;

    public ResponseResult generatorCode(String passengerPhone) {
        //调用验证码服务 - 微服務間的調用，获取6位数字验证码。
        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerificationCodeClient.getNumberCode(6);
        int numberCode = numberCodeResponse.getData().getNumberCode();

        // save to redis: key, value, expire time
        String key = RedisPrefixUtils.generatePassengerKeyByPhone(passengerPhone);
        stringRedisTemplate.opsForValue().set(key, numberCode + "", 2, TimeUnit.MINUTES);

        // todo: send message, 可以调用阿里短信服务，腾讯短信通
        return ResponseResult.success();
    }

    public ResponseResult checkCode(String passengerPhone, String verificationCode) {
        String key = RedisPrefixUtils.generatePassengerKeyByPhone(passengerPhone);

        // 根据手机号， 从redis里拿数据
        String codeRedis = stringRedisTemplate.opsForValue().get(key);

        // 校验验证码
        if (StringUtils.isBlank(codeRedis)) {
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(), CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());
        }
        if (!verificationCode.trim().equals(codeRedis.trim())) {
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(), CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());
        }

        // 判断原来是否有用户，并进行相应的处理
        VerificationCodeDTO verificationCodeDTO = new VerificationCodeDTO();
        verificationCodeDTO.setPassengerPhone(passengerPhone);
        servicePassengerUserClient.loginOrRegister(verificationCodeDTO);

        // 颁发令牌
        String accessToken = JwtUtils.generateToken(passengerPhone, IdentityConstant.PASSENGER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);
        String refreshToken = JwtUtils.generateToken(passengerPhone, IdentityConstant.PASSENGER_IDENTITY, TokenConstants.REFRESH_TOKEN_TYPE);

        // 将token存储到redis中
        String accessTokenKey = RedisPrefixUtils.generateTokeyKey(passengerPhone,  IdentityConstant.PASSENGER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);
        String refreshTokenKey = RedisPrefixUtils.generateTokeyKey(passengerPhone,  IdentityConstant.PASSENGER_IDENTITY, TokenConstants.REFRESH_TOKEN_TYPE);
        // 这里增加了有限效后，就不需要给token本身设置有效期了
        stringRedisTemplate.opsForValue().set(accessTokenKey, accessToken, 30, TimeUnit.DAYS);
        // 这里refreshToken比accessToken的有效期要长一点点
        stringRedisTemplate.opsForValue().set(refreshTokenKey, refreshToken, 31, TimeUnit.DAYS);


        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        return ResponseResult.success(tokenResponse);
    }
}
