package com.sqin.servicedriveruser.remote;

import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.response.TerminalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("service-map")
public interface ServiceMapClient {

    @PostMapping("/terminal/add")
    public ResponseResult<TerminalResponse> add(@RequestParam String name);

}
