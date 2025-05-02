package com.sqin.serviceorder.remote;

import com.sqin.internalcommon.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("service-driver-user")
public interface ServiceDriverUserClient {

    @GetMapping("/city-driver/is-available-driver")
    public ResponseResult isAvailableDriver(@RequestParam String cityCode);

}
