package com.sqin.internalcommon.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Qin
 * @since 2025-04-30
 */
@Data
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 绑定的终端id
     */
    private String tid;

    private String trid;

    private String trname;

    private String address;

    private String vehicleNo;

    /**
     * 车牌颜色，1蓝色，2黄色，3黑色，4白色，5绿色
     */
    private String plateColor;

    private Integer seats;

    private String brand;

    private String model;

    private String vehicleType;

    private String ownerName;

    private String vehicleColor;

    private String engineId;

    private String vin;

    private LocalDate certifyDateA;

    /**
     * 燃料类型
     */
    private String fueType;

    private String engineDisplace;

    private String transAgency;

    private String transArea;

    private LocalDate transDateStart;

    private LocalDateTime transDateEnd;

    private LocalDate certifyDateB;

    private String fixState;

    private LocalDate nextFixDate;

    private String checkState;

    private String feePrintId;

    private String gpsBrand;

    private String gpsModel;

    private LocalDate registerDate;

    private LocalDate gpsInstallDate;

    private Integer commercialType;

    /**
     * 关联计价规则
     */
    private String fareType;

    /**
     * 0有效，1无效
     */
    private Integer state;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

}
