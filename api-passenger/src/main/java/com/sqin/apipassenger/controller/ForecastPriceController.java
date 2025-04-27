package com.sqin.apipassenger.controller;

import com.sqin.apipassenger.service.ForecastPriceService;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.ForecastPriceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ForecastPriceController {

    @Autowired
    private ForecastPriceService forecastPriceService;

    @PostMapping("/forecast-price")
    public ResponseResult forecastPrice(@RequestBody ForecastPriceDTO forecastPriceDTO) {
        log.info("出发地经度:" + forecastPriceDTO.getDepLongitude());

        String depLatitude = forecastPriceDTO.getDepLatitude();
        String depLongitude = forecastPriceDTO.getDepLongitude();
        String destLatitude = forecastPriceDTO.getDestLatitude();
        String destLongitude = forecastPriceDTO.getDestLongitude();

        return forecastPriceService.forecastPrice(depLatitude, depLongitude, destLatitude, destLongitude);
    }

}
