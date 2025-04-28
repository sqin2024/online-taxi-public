package com.sqin.apipassenger.service;

import com.sqin.apipassenger.remote.ServicePriceClient;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.ForecastPriceDTO;
import com.sqin.internalcommon.response.ForecastPriceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForecastPriceService {

    @Autowired
    private ServicePriceClient servicePriceClient;

    /**
     * 根据出发地和目的地经纬度，计算预估价格
     *
     * @param depLongitude
     * @param depLatitude
     * @param destLongitude
     * @param destLatitude
     * @return
     */
    public ResponseResult<ForecastPriceResponse> forecastPrice(String depLongitude, String depLatitude, String destLongitude, String destLatitude) {

        // 调用计价服务
        ForecastPriceDTO forecastPriceDTO = new ForecastPriceDTO();
        forecastPriceDTO.setDepLongitude(depLongitude);
        forecastPriceDTO.setDepLatitude(depLatitude);
        forecastPriceDTO.setDestLongitude(destLongitude);
        forecastPriceDTO.setDestLatitude(destLatitude);

        ResponseResult<ForecastPriceResponse> forecastPrice = servicePriceClient.forecastPrice(forecastPriceDTO);
        double price = forecastPrice.getData().getPrice();

        ForecastPriceResponse forecastPriceResponse = new ForecastPriceResponse();
        forecastPriceResponse.setPrice(price);

        return ResponseResult.success(forecastPriceResponse);
    }

}
