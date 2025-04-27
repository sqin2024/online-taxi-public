package com.sqin.servicepassengeruser.controller;

import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.VerificationCodeDTO;
import com.sqin.servicepassengeruser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public ResponseResult loginOrRegister(@RequestBody VerificationCodeDTO verificationCodeDTO) {
        String passengerPhone = verificationCodeDTO.getPassengerPhone();
        return userService.loginOrRegister(passengerPhone);
    }

    @GetMapping("/users/{phone}")
    public ResponseResult getUser(@PathVariable("phone") String passengerPhone) {
        return userService.getUserByPhone(passengerPhone);
    }

}
