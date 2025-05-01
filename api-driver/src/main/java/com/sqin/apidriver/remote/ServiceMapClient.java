package com.sqin.apidriver.remote;

import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.PointRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("service-map")
public interface ServiceMapClient {

    @PostMapping("/point/upload")
    public ResponseResult upload(@RequestBody PointRequest pointRequest);

}
