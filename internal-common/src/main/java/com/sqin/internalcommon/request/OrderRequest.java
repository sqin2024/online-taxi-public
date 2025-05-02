package com.sqin.internalcommon.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderRequest {

    private Long passengerId;

    private String passengerPhone;

    private String address;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    private String departure;

    private String depLongitude;

    private String depLatitude;

    private String destination;

    private String destLongitude;

    private String destLatitude;

    // 坐标加密标识， 1：gcj-02，2：wgs84，3：bd-09，4：cgcs2000北斗，0：其他
    private Integer encrypt;

    private Integer fareVersion;

    private String fareType;

    private String deviceCode;

}
