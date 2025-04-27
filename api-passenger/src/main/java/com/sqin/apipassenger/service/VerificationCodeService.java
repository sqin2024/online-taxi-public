package com.sqin.apipassenger.service;

import com.sqin.apipassenger.remote.ServicePassengerUserClient;
import com.sqin.apipassenger.remote.ServiceVerificationCodeClient;
import com.sqin.internalcommon.constant.CommonStatusEnum;
import com.sqin.internalcommon.constant.IdentityConstant;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.VerificationCodeDTO;
import com.sqin.internalcommon.response.NumberCodeResponse;
import com.sqin.internalcommon.response.TokenResponse;
import com.sqin.internalcommon.util.JwtUtils;
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

    private String verificationCodePrefix = "passenger-verification-code-";

    private String tokenPrefix = "token-";

    public ResponseResult generatorCode(String passengerPhone) {
        //调用验证码服务 - 微服務間的調用，获取6位数字验证码。
        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerificationCodeClient.getNumberCode(6);
        int numberCode = numberCodeResponse.getData().getNumberCode();

        // save to redis: key, value, expire time
        String key = generateKeyByPhone(passengerPhone);
        stringRedisTemplate.opsForValue().set(key, numberCode + "", 2, TimeUnit.MINUTES);

        // todo: send message, 可以调用阿里短信服务，腾讯短信通
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
        VerificationCodeDTO verificationCodeDTO = new VerificationCodeDTO();
        verificationCodeDTO.setPassengerPhone(passengerPhone);
        servicePassengerUserClient.loginOrRegister(verificationCodeDTO);

        // 颁发令牌
        String token = JwtUtils.generateToken(passengerPhone, IdentityConstant.PASSENGER_IDENTITY);

        // 将token存储到redis中
        String tokenKey = generateTokeyKey(passengerPhone,  IdentityConstant.PASSENGER_IDENTITY);
        // 这里增加了有限效后，就不需要给token本身设置有效期了
        stringRedisTemplate.opsForValue().set(tokenKey, token, 30, TimeUnit.DAYS);



        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(token);
        return ResponseResult.success(tokenResponse);
    }

    /**
     * 根据手机号，生成验证码的redis key
     * @param passengerPhone
     * @return
     */
    private String generateKeyByPhone(String passengerPhone) {
        return verificationCodePrefix + passengerPhone;
    }

    /**
     * 根据手机号和身份标识，生成token
     * @param phone
     * @param identity
     * @return
     */
    private String generateTokeyKey(String phone, String identity) {
        return tokenPrefix + phone + "-" + identity;
    }
}
