package com.sqin.apidriver.service;

import com.sqin.internalcommon.dto.ResponseResult;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeService {

    public ResponseResult checkAndSendVerificationCode(String driverPhone) {
        // 查询service-driver-user， 该手机号的手机是否存在

        // 获取验证码

        // 调用第三方发送验证码

        // 存入Redis

        return ResponseResult.success("");
    }

}
