package com.sqin.servicepassengeruser.service;

import com.sqin.internalcommon.dto.ResponseResult;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public ResponseResult loginOrRegister(String passengerPhone) {

        System.out.println("user service");

        return ResponseResult.success();
    }

}
