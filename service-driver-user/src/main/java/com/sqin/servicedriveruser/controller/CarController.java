package com.sqin.servicedriveruser.controller;


import com.sqin.internalcommon.dto.Car;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.servicedriveruser.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.awt.geom.RectangularShape;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Qin
 * @since 2025-04-30
 */
@Controller
@RestController
public class CarController {

    @Autowired
    private CarService carService;

    @PostMapping("/car")
    public ResponseResult addCar(@RequestBody Car car) {
        return carService.addCar(car);
    }

}
