package com.sqin.servicepassengeruser.controller;

import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.VerificationCodeDTO;
import com.sqin.servicepassengeruser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public ResponseResult loginOrRegister(@RequestBody VerificationCodeDTO verificationCodeDTO) {
        String passengerPhone = verificationCodeDTO.getPassengerPhone();
        System.out.println("passengerPhone : " + passengerPhone);
        return userService.loginOrRegister(passengerPhone);
    }

}
