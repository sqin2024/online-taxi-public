package com.sqin.apiboss.service;

import com.sqin.apiboss.remote.ServiceDriverUserClient;
import com.sqin.internalcommon.dto.Car;
import com.sqin.internalcommon.dto.DriverCarBindingRelationship;
import com.sqin.internalcommon.dto.DriverUser;
import com.sqin.internalcommon.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverUserService {

    @Autowired
    private ServiceDriverUserClient serviceDriverUserClient;

    public ResponseResult addDriverUser(DriverUser driverUser) {
        return serviceDriverUserClient.addUser(driverUser);
    }

    public ResponseResult updateDriverUser(DriverUser driverUser) {
        return serviceDriverUserClient.updateUser(driverUser);
    }

    public ResponseResult addCar(Car car) {
        return serviceDriverUserClient.addCar(car);
    }

    public ResponseResult bind(DriverCarBindingRelationship driverCarBindingRelationship) {
        return serviceDriverUserClient.bind(driverCarBindingRelationship);
    }

    public ResponseResult unbind(DriverCarBindingRelationship driverCarBindingRelationship) {
        return serviceDriverUserClient.unbind(driverCarBindingRelationship);
    }

}
