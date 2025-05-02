package com.sqin.serviceorder.service;

import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.OrderRequest;
import com.sqin.internalcommon.dto.OrderInfo;
import com.sqin.serviceorder.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    public ResponseResult add(OrderRequest orderRequest) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setAddress("110000");

        orderMapper.insert(new OrderInfo());
        return ResponseResult.success();
    }


}
