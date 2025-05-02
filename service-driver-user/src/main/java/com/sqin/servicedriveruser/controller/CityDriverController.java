package com.sqin.servicedriveruser.controller;

import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.servicedriveruser.service.CityDriverUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/city-driver")
public class CityDriverController {

    @Autowired
    private CityDriverUserService cityDriverUserService;

    @GetMapping("/is-available-driver")
    public ResponseResult isAvailableDriver(String cityCode) {
        return cityDriverUserService.isAvailableDriver(cityCode);
    }

}
