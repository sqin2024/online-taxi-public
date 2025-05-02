package com.sqin.apipassenger.service;

import com.sqin.apipassenger.remote.ServiceOrderClient;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private ServiceOrderClient serviceOrderClient;

    public ResponseResult add(OrderRequest orderRequest) {
        return serviceOrderClient.add(orderRequest);
    }

}
