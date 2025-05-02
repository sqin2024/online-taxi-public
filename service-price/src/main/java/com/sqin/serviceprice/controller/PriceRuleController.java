package com.sqin.serviceprice.controller;

import com.sqin.internalcommon.dto.PriceRule;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.PriceRuleIsNewRequest;
import com.sqin.serviceprice.service.PriceRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/price-rule")
public class PriceRuleController {

    @Autowired
    private PriceRuleService priceRuleService;

    @PostMapping("/add")
    public ResponseResult add(@RequestBody PriceRule priceRule) {
        return priceRuleService.add(priceRule);
    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody PriceRule priceRule) {
        return priceRuleService.update(priceRule);
    }

    /**
     * 查询最新的计价规则
     * @param fareType
     * @return
     */
    @GetMapping("/get-newest-version")
    public ResponseResult<PriceRule> getNewestVersion(@RequestParam String fareType){
        return priceRuleService.getNewestVersion(fareType);
    }

    /**
     * 判断规则是否是最新
     * @param priceRuleIsNewRequest
     * @return
     */
    @PostMapping("/is-new")
    public ResponseResult<Boolean> isNew(@RequestBody PriceRuleIsNewRequest priceRuleIsNewRequest){
        return priceRuleService.isNew(priceRuleIsNewRequest.getFareType(), priceRuleIsNewRequest.getFareVersion());
    }


    @GetMapping("/if-exists")
    public ResponseResult<Boolean> ifExists(@RequestBody PriceRule priceRule) {
        return priceRuleService.ifExists(priceRule);
    }

}
