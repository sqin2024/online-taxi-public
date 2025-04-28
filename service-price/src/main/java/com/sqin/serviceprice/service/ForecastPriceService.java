package com.sqin.serviceprice.service;

import com.sqin.internalcommon.constant.CommonStatusEnum;
import com.sqin.internalcommon.dto.PriceRule;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.ForecastPriceDTO;
import com.sqin.internalcommon.response.DirectionResponse;
import com.sqin.internalcommon.response.ForecastPriceResponse;
import com.sqin.serviceprice.mapper.PriceRuleMapper;
import com.sqin.serviceprice.remote.ServiceMapClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ForecastPriceService {

    @Autowired
    private ServiceMapClient serviceMapClient;

    @Autowired
    private PriceRuleMapper priceRuleMapper;

    public ResponseResult<ForecastPriceResponse> forecastPrice(String depLongitude, String depLatitude, String destLongitude, String destLatitude) {

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
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("city_code", "110000");
        queryMap.put("vehicle_type", "1");
        List<PriceRule> priceRules = priceRuleMapper.selectByMap(queryMap);
        if (priceRules.size() == 0 ) {
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_EMPTY.getCode(), CommonStatusEnum.PRICE_RULE_EMPTY.getValue());
        }

        // 调用计价服务
        PriceRule priceRule = priceRules.get(0);
        double price = getPrice(distance, duration, priceRule);

        ForecastPriceResponse response = new ForecastPriceResponse();
        response.setPrice(price);
        return ResponseResult.success(response);
    }

    /**
     * 根据距离，时长，计价规则，计算最终价格
     * @param distance
     * @param duration
     * @param priceRule
     * @return
     */
    private double getPrice(Integer distance, Integer duration, PriceRule priceRule) {
        BigDecimal price = new BigDecimal(0);

        // 起步价
        Double startFare = priceRule.getStartFare();
        BigDecimal startFareDecimal = new BigDecimal(startFare);
        price = price.add(startFareDecimal);

        // 里程费
        BigDecimal distanceDecimal = new BigDecimal(distance);
        // 总里程 km
        BigDecimal distanceMileDecimal = distanceDecimal.divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP);
        // 起步里程
        Integer startMile = priceRule.getStartMile();
        BigDecimal startMileDecimal = new BigDecimal(startMile);
        Double distanceSubtract = distanceMileDecimal.subtract(startMileDecimal).doubleValue();
        // 最终收费里程数，没到起步里程，按0计算
        Double mile = distanceSubtract > 0 ? distanceSubtract : 0;
        BigDecimal mileDecimal = new BigDecimal(mile);
        // 每公里多少钱
        Double unitPricePerMile = priceRule.getUnitPricePerMile();
        BigDecimal unitPricePerMileDecimal = new BigDecimal(unitPricePerMile);
        // 里程价格： 每公里多少钱 * 最终里程
        BigDecimal mileFare = mileDecimal.multiply(unitPricePerMileDecimal).setScale(2, BigDecimal.ROUND_HALF_UP);
        price = price.add(mileFare);

        // 时长费
        BigDecimal time = new BigDecimal(duration);
        // 时长单位是秒，换算成分钟。
        BigDecimal timeDecimal = time.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP);
        Double unitPricePerMinute = priceRule.getUnitPricePerMinute();
        BigDecimal unitPricePerMinuteDecimal = new BigDecimal(unitPricePerMinute);
        BigDecimal timeFare = timeDecimal.multiply(unitPricePerMinuteDecimal);
        price = price.add(timeFare).setScale(2, BigDecimal.ROUND_HALF_UP);

        return price.doubleValue();
    }

}
