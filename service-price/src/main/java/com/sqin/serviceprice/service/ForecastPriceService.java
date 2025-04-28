package com.sqin.serviceprice.service;

import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.response.ForecastPriceResponse;
import org.springframework.stereotype.Service;

@Service
public class ForecastPriceService {

    public ResponseResult forecastPrice(String depLongitude, String depLatitude, String destLongitude, String destLatitude) {

        // 调用地图服务，查询距离和时长

        // 读取计价规则

        // 根据距离时长和计价规则，计算价格

        // 调用计价服务

        ForecastPriceResponse response = new ForecastPriceResponse();
        response.setPrice(23.32);


        return ResponseResult.success(response);
    }

}
