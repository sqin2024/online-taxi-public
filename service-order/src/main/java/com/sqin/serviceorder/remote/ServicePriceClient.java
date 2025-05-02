package com.sqin.serviceorder.remote;

import com.sqin.internalcommon.dto.PriceRule;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.PriceRuleIsNewRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("service-price")
public interface ServicePriceClient {

    @PostMapping("/price-rule/is-new")
    public ResponseResult<Boolean> isNew(@RequestBody PriceRuleIsNewRequest priceRuleIsNewRequest);

    @PostMapping("/price-rule/if-exists")
    public ResponseResult<Boolean> ifExists(@RequestBody PriceRule priceRule);

}
