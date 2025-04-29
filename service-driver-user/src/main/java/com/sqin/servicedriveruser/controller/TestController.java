package com.sqin.servicedriveruser.controller;

import com.sqin.internalcommon.dto.DriverUser;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.servicedriveruser.service.DriverUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private DriverUserService driverUserService;

    @GetMapping("/test")
    public ResponseResult<DriverUser> test(){
        return driverUserService.testGetDriver();
    }


}
