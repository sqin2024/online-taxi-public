package com.sqin.apipassenger.service;

import com.sqin.apipassenger.remote.ServiceVerificationCodeClient;
import com.sqin.internalcommon.constant.CommonStatusEnum;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.response.NumberCodeResponse;
import com.sqin.internalcommon.response.TokenResponse;
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

    private String verificationCodePrefix = "passenger-verification-code-";

    public ResponseResult generatorCode(String passengerPhone) {
        //调用验证码服务
        System.out.println("获取验证码");
        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerificationCodeClient.getNumberCode(6);
        int numberCode = numberCodeResponse.getData().getNumberCode();

        System.out.println("remote number code: " + numberCode);

        // save to redis: key, value, expire time
        String key = generateKeyByPhone(passengerPhone);
        stringRedisTemplate.opsForValue().set(key, numberCode + "", 2, TimeUnit.MINUTES);

        // send message, 可以调用阿里短信服务，腾讯短信通

        return ResponseResult.success();
    }

    public ResponseResult checkCode(String passengerPhone, String verificationCode) {
        String key = generateKeyByPhone(passengerPhone);

        // 根据手机号， 从redis里拿数据
        String codeRedis = stringRedisTemplate.opsForValue().get(key);
        System.out.println("get code from redis, code = " + codeRedis);

        // 校验验证码
        if (StringUtils.isBlank(codeRedis)) {
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(), CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());
        }

        if (!verificationCode.trim().equals(codeRedis.trim())) {
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(), CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());
        }

        // 判断原来是否有用户，并进行相应的处理

        // 颁发令牌

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken("test token value");
        return ResponseResult.success(tokenResponse);
    }

    private String generateKeyByPhone(String passengerPhone) {
        return verificationCodePrefix + passengerPhone;
    }
}
