package com.sqin.servicedriveruser.controller;

import com.sqin.internalcommon.constant.DriverCarConstants;
import com.sqin.internalcommon.dto.DriverUser;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.response.DriverUserExistsResponse;
import com.sqin.servicedriveruser.mapper.DriverUserMapper;
import com.sqin.servicedriveruser.service.DriverUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private DriverUserService driverUserService;

    @PostMapping("/user")
    public ResponseResult addUser(@RequestBody DriverUser driverUser) {
        return driverUserService.addDriverUser(driverUser);
    }

    @PutMapping("/user")
    public ResponseResult updateUser(@RequestBody DriverUser driverUser) {
        return driverUserService.updateDriverUser(driverUser);
    }

    @GetMapping("/check-driver/{driverPhone}")
    public ResponseResult<DriverUserExistsResponse> getUser(@PathVariable("driverPhone") String driverPhone) {
        ResponseResult<DriverUser> driverUserByPhone = driverUserService.getDriverUserByPhone(driverPhone);
        DriverUser driverUserDB = driverUserByPhone.getData();
        int ifExists = DriverCarConstants.DRIVER_EXITS;
        if(driverUserDB == null) {
            ifExists = DriverCarConstants.DRIVER_NOT_EXISTS;
        }
        DriverUserExistsResponse response = new DriverUserExistsResponse();
        response.setDriverPhone(driverPhone);
        response.setIfExists(ifExists);
        return ResponseResult.success(response);
    }



}
