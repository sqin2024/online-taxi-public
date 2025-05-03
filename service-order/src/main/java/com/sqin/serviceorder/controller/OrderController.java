package com.sqin.serviceorder.controller;

import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.OrderRequest;
import com.sqin.serviceorder.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/add")
    public ResponseResult add(@RequestBody OrderRequest orderRequest, HttpServletRequest httpServletRequest) {
//        String deviceCode = httpServletRequest.getHeader(HeaderParamConstant.DEVICE_CODE);
//        orderRequest.setDeviceCode(deviceCode);
//        System.out.println(deviceCode);

        System.out.println("service-order" + orderRequest.getAddress());
        return orderService.add(orderRequest);
    }

    @PostMapping("/to-pick-up-passenger")
    public ResponseResult changeStatus(@RequestBody OrderRequest orderRequest) {
        return orderService.toPickUpPassenger(orderRequest);
    }

}
