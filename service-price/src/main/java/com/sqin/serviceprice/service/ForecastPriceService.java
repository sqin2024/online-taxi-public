package com.sqin.serviceprice.service;

import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.ForecastPriceDTO;
import com.sqin.internalcommon.response.DirectionResponse;
import com.sqin.internalcommon.response.ForecastPriceResponse;
import com.sqin.serviceprice.remote.ServiceMapClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ForecastPriceService {

    @Autowired
    private ServiceMapClient serviceMapClient;

    public ResponseResult forecastPrice(String depLongitude, String depLatitude, String destLongitude, String destLatitude) {

        // 调用地图服务，查询距离和时长
        ForecastPriceDTO forecastPriceDTO = new ForecastPriceDTO();
        forecastPriceDTO.setDepLatitude(depLatitude);
        forecastPriceDTO.setDepLongitude(depLongitude);
        forecastPriceDTO.setDestLatitude(destLatitude);
        forecastPriceDTO.setDestLongitude(destLongitude);
        ResponseResult<DirectionResponse> direction = serviceMapClient.direction(forecastPriceDTO);
        // 读取计价规则
        Integer distance = direction.getData().getDistance();
        Integer duration = direction.getData().getDuration();
        log.info("distance: " + distance + ", duration: " + duration);

        // 根据距离时长和计价规则，计算价格

        // 调用计价服务

        ForecastPriceResponse response = new ForecastPriceResponse();
        response.setPrice(23.32);


        return ResponseResult.success(response);
    }

}
