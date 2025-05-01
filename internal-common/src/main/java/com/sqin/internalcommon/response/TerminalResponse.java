package com.sqin.internalcommon.response;

import lombok.Data;

@Data
public class TerminalResponse {
    private Long carId;
    private String tid;
    private String longitude;
    private String latitude;

}
