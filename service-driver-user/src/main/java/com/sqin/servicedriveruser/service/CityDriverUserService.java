package com.sqin.servicedriveruser.service;

import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.servicedriveruser.mapper.DriverUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityDriverUserService {

    @Autowired
    private DriverUserMapper driverUserMapper;

    public ResponseResult<Boolean> isAvailableDriver(String cityCode) {
        int i = driverUserMapper.selectDriverUserCountByCityCode(cityCode);
        if (i > 0) {
            return ResponseResult.success(true);
        } else {
            return ResponseResult.success(false);
        }
    }

}
