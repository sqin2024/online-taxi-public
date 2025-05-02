package com.sqin.apipassenger.remote;

import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.OrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("service-order")
public interface ServiceOrderClient {

    @PostMapping("/order/add")
    public ResponseResult add(@RequestBody OrderRequest orderRequest);

}
