package com.sqin.serviceorder.remote;

import com.sqin.internalcommon.request.PushRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("service-sse-push")
public interface ServiceSsePushClient {


    @PostMapping("/push")
    public String push(@RequestBody PushRequest pushRequest);

}
