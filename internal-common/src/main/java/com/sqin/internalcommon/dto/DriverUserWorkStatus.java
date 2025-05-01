package com.sqin.internalcommon.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Qin
 * @since 2025-05-01
 */
@Data
public class DriverUserWorkStatus implements Serializable {

    private Long id;

    private Long driverId;

    private Integer workStatus;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
