package com.sqin.internalcommon.request;

import lombok.Data;

@Data
public class PriceRuleIsNewRequest {

    private String fareType;

    private Integer fareVersion;
}