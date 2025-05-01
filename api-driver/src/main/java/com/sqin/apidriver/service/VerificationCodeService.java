package com.sqin.apidriver.service;

import com.sqin.apidriver.remote.ServiceDriverUserClient;
import com.sqin.apidriver.remote.ServiceVerificationCodeClient;
import com.sqin.internalcommon.constant.CommonStatusEnum;
import com.sqin.internalcommon.constant.DriverCarConstants;
import com.sqin.internalcommon.constant.IdentityConstant;
import com.sqin.internalcommon.constant.TokenConstants;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.response.DriverUserExistsResponse;
import com.sqin.internalcommon.response.NumberCodeResponse;
import com.sqin.internalcommon.response.TokenResponse;
import com.sqin.internalcommon.util.JwtUtils;
import com.sqin.internalcommon.util.RedisPrefixUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class VerificationCodeService {

    @Autowired
    private ServiceDriverUserClient driverUserClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ServiceVerificationCodeClient serviceVerificationCodeClient;

    /**
     * 查看当前手机号的司机是否已存在，并发送验证码
     * @param driverPhone
     * @return
     */
    public ResponseResult checkAndSendVerificationCode(String driverPhone) {
        // 查询service-driver-user， 该手机号的手机是否存在
        ResponseResult<DriverUserExistsResponse> user = driverUserClient.getUser(driverPhone);
        DriverUserExistsResponse driverUserExistsResponse = user.getData();
        int ifExists = driverUserExistsResponse.getIfExists();
        if (ifExists == DriverCarConstants.DRIVER_NOT_EXISTS) {
            return ResponseResult.fail(CommonStatusEnum.DRIVER_NOT_EXIST.getCode(), CommonStatusEnum.DRIVER_NOT_EXIST.getValue());
        }
        // 获取验证码
        ResponseResult<NumberCodeResponse> numberCodeResponse = this.serviceVerificationCodeClient.getNumberCode(6);
        int numberCode = numberCodeResponse.getData().getNumberCode();

        // save to redis: key, value, expire time
        String key = RedisPrefixUtils.generateDriverKeyByPhone(driverPhone);
        stringRedisTemplate.opsForValue().set(key, numberCode + "", 2, TimeUnit.MINUTES);

        // 调用第三方发送验证码
        return ResponseResult.success(numberCode);
    }

    public ResponseResult checkCode(String driverPhone, String verificationCode) {
        String key = RedisPrefixUtils.generateDriverKeyByPhone(driverPhone);

        // 根据手机号， 从redis里拿数据
        String codeRedis = stringRedisTemplate.opsForValue().get(key);

        // 校验验证码
        if (StringUtils.isBlank(codeRedis)) {
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(), CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());
        }
        if (!verificationCode.trim().equals(codeRedis.trim())) {
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(), CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());
        }

        // 颁发令牌
        String accessToken = JwtUtils.generateToken(driverPhone, IdentityConstant.DRIVER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);
        String refreshToken = JwtUtils.generateToken(driverPhone, IdentityConstant.DRIVER_IDENTITY, TokenConstants.REFRESH_TOKEN_TYPE);

        // 将token存储到redis中
        String accessTokenKey = RedisPrefixUtils.generateTokeyKey(driverPhone,  IdentityConstant.DRIVER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);
        String refreshTokenKey = RedisPrefixUtils.generateTokeyKey(driverPhone,  IdentityConstant.DRIVER_IDENTITY, TokenConstants.REFRESH_TOKEN_TYPE);
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
