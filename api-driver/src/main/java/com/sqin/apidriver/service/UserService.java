package com.sqin.apidriver.service;

import com.sqin.apidriver.remote.ServiceDriverUserClient;
import com.sqin.internalcommon.dto.DriverUser;
import com.sqin.internalcommon.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private ServiceDriverUserClient serviceDriverUserClient;

    public ResponseResult addUser(DriverUser driverUser) {
        return serviceDriverUserClient.addUser(driverUser);
    }

    public ResponseResult updateUser(DriverUser driverUser) {
        return serviceDriverUserClient.updateUser(driverUser);
    }

}
