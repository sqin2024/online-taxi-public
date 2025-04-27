package com.sqin.apipassenger.service;

import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.response.ForecastPriceResponse;
import org.springframework.stereotype.Service;

@Service
public class ForecastPriceService {

    /**
     * 根据出发地和目的地经纬度，计算预估价格
     *
     * @param depLongitude
     * @param depLatitude
     * @param destLongitude
     * @param destLatitude
     * @return
     */
    public ResponseResult forecastPrice(String depLongitude, String depLatitude, String destLongitude, String destLatitude) {

        // 调用计价服务

        ForecastPriceResponse response = new ForecastPriceResponse();
        response.setPrice(23.32);


        return ResponseResult.success(response);
    }

}
