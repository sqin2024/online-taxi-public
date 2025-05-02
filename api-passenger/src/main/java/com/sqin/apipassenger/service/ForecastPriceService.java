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
    public ResponseResult<ForecastPriceResponse> forecastPrice(String depLongitude, String depLatitude, String destLongitude, String destLatitude, String cityCode, String vehicleType) {

        // 调用计价服务
        ForecastPriceDTO forecastPriceDTO = new ForecastPriceDTO();
        forecastPriceDTO.setDepLongitude(depLongitude);
        forecastPriceDTO.setDepLatitude(depLatitude);
        forecastPriceDTO.setDestLongitude(destLongitude);
        forecastPriceDTO.setDestLatitude(destLatitude);
        forecastPriceDTO.setCityCode(cityCode);
        forecastPriceDTO.setVehicleType(vehicleType);

        ResponseResult<ForecastPriceResponse> forecastPrice = servicePriceClient.forecastPrice(forecastPriceDTO);

        ForecastPriceResponse forecastPriceData = forecastPrice.getData();
        double price = forecastPriceData.getPrice();

        ForecastPriceResponse forecastPriceResponse = new ForecastPriceResponse();
        forecastPriceResponse.setPrice(price);
        forecastPriceResponse.setVehicleType(vehicleType);
        forecastPriceResponse.setCityCode(cityCode);
        forecastPriceResponse.setFareVersion(forecastPriceData.getFareVersion());
        forecastPriceResponse.setFareType(forecastPriceData.getFareType());

        return ResponseResult.success(forecastPriceResponse);
    }

}
