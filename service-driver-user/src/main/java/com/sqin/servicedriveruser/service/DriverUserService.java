package com.sqin.servicedriveruser.service;

import com.sqin.internalcommon.dto.DriverUser;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.servicedriveruser.mapper.DriverUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DriverUserService {

    @Autowired
    private DriverUserMapper driverUserMapper;

    public ResponseResult<DriverUser> testGetDriver() {
        DriverUser driverUser = driverUserMapper.selectById("1");
        return ResponseResult.success(driverUser);
    }

    public ResponseResult addDriverUser(DriverUser driverUser) {
        LocalDateTime now = LocalDateTime.now();
        driverUser.setGmtCreate(now);
        driverUser.setGmtModified(now);
        int insert = driverUserMapper.insert(driverUser);
        return ResponseResult.success(insert);
    }

}
