package com.sqin.apidriver.service;

import com.sqin.apidriver.remote.ServiceDriverUserClient;
import com.sqin.apidriver.remote.ServiceVerificationCodeClient;
import com.sqin.internalcommon.constant.CommonStatusEnum;
import com.sqin.internalcommon.constant.DriverCarConstants;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.response.DriverUserExistsResponse;
import com.sqin.internalcommon.response.NumberCodeResponse;
import com.sqin.internalcommon.util.RedisPrefixUtils;
import lombok.extern.slf4j.Slf4j;
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

        return ResponseResult.success("");
    }

}
