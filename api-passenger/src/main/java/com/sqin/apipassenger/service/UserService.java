package com.sqin.apipassenger.service;

import com.sqin.apipassenger.remote.ServicePassengerUserClient;
import com.sqin.internalcommon.dto.PassengerUser;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.dto.TokenResult;
import com.sqin.internalcommon.request.VerificationCodeDTO;
import com.sqin.internalcommon.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    private ServicePassengerUserClient servicePassengerUserClient;

    public ResponseResult getUserByAccessToken(String accessToken) {
        log.info("accessToken:" + accessToken);
        //解析token，拿到手机号
        TokenResult tokenResult = JwtUtils.checkToken(accessToken);
        String phone = tokenResult.getPhone();
        log.info("phone : " + phone);

        return servicePassengerUserClient.getUser(phone);
    }

}
