package com.sqin.serviceorder.service;

import com.sqin.internalcommon.constant.OrderConstants;
import com.sqin.internalcommon.dto.OrderInfo;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.OrderRequest;
import com.sqin.serviceorder.mapper.OrderMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    public ResponseResult add(OrderRequest orderRequest) {
        OrderInfo orderInfo = new OrderInfo();

        BeanUtils.copyProperties(orderRequest, orderInfo);
        orderInfo.setOrderStatus(OrderConstants.ORDER_START);
        LocalDateTime now = LocalDateTime.now();
        orderInfo.setGmtModified(now);
        orderInfo.setGmtCreate(now);

        orderMapper.insert(orderInfo);
        return ResponseResult.success();
    }


}
