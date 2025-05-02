package com.sqin.serviceorder.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sqin.internalcommon.constant.CommonStatusEnum;
import com.sqin.internalcommon.constant.OrderConstants;
import com.sqin.internalcommon.dto.OrderInfo;
import com.sqin.internalcommon.dto.PriceRule;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.OrderRequest;
import com.sqin.internalcommon.request.PriceRuleIsNewRequest;
import com.sqin.internalcommon.util.RedisPrefixUtils;
import com.sqin.serviceorder.mapper.OrderMapper;
import com.sqin.serviceorder.remote.ServicePriceClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ServicePriceClient servicePriceClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public ResponseResult add(OrderRequest orderRequest) {

        /**
         * 判断下单的城市和计价规则是否存在
         */
        if (!isPriceRuleExists(orderRequest)) {
            return ResponseResult.fail(CommonStatusEnum.CITY_SERVICE_NOT_SERVICE.getCode(), CommonStatusEnum.CITY_SERVICE_NOT_SERVICE.getValue());
        }

        /**
         * 判断预估价格时，使用的计价规则不是最新的，需要重新估价
         */
        PriceRuleIsNewRequest priceRuleIsNewRequest = new PriceRuleIsNewRequest();
        priceRuleIsNewRequest.setFareType(orderRequest.getFareType());
        priceRuleIsNewRequest.setFareVersion(orderRequest.getFareVersion());
        ResponseResult<Boolean> aNew = servicePriceClient.isNew(priceRuleIsNewRequest);
        if (!aNew.getData()) {
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_CHANGED.getCode(), CommonStatusEnum.PRICE_RULE_CHANGED.getValue());
        }

        /**
         * 判断有正在进行的订单不允许下单
         */
        if (isPassengerOrderGoingon(orderRequest.getPassengerId()) > 0) {
            return ResponseResult.fail(CommonStatusEnum.ORDER_GOING_ON.getCode(), CommonStatusEnum.ORDER_GOING_ON.getValue());
        }

        /**
         * 是否是黑名单设备
         */
        if (isBlackDevice(orderRequest)) {
            return ResponseResult.fail(CommonStatusEnum.DEVICE_IS_BLACK.getCode(), CommonStatusEnum.DEVICE_IS_BLACK.getValue());
        }

        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderRequest, orderInfo);
        orderInfo.setOrderStatus(OrderConstants.ORDER_START);
        LocalDateTime now = LocalDateTime.now();
        orderInfo.setGmtModified(now);
        orderInfo.setGmtCreate(now);

        orderMapper.insert(orderInfo);
        return ResponseResult.success();
    }

    private boolean isPriceRuleExists(OrderRequest orderRequest) {
        String fareType = orderRequest.getFareType();
        int index = fareType.indexOf("$");
        String cityCode = fareType.substring(0, index);
        String vehicleType = fareType.substring(index + 1);

        PriceRule priceRule = new PriceRule();
        priceRule.setCityCode(cityCode);
        priceRule.setVehicleType(vehicleType);

        ResponseResult<Boolean> booleanResponseResult = servicePriceClient.ifExists(priceRule);
        return booleanResponseResult.getData();
    }

    /**
     * 是否是黑名单
     *
     * @param orderRequest
     * @return
     */
    private boolean isBlackDevice(OrderRequest orderRequest) {
        String deviceCode = orderRequest.getDeviceCode();
        // 生成key
        String deviceCodeKey = RedisPrefixUtils.generateBlackDeviceCode(deviceCode);
        Boolean aBoolean = stringRedisTemplate.hasKey(deviceCodeKey);
        if (aBoolean) {
            String s = stringRedisTemplate.opsForValue().get(deviceCodeKey);
            int i = Integer.parseInt(s);
            if (i >= 2) {
                // 当前设备超过下单次数
                return true;
            } else {
                stringRedisTemplate.opsForValue().increment(deviceCodeKey);
            }
        } else {
            stringRedisTemplate.opsForValue().setIfAbsent(deviceCodeKey, "1", 1L, TimeUnit.HOURS);
        }
        return false;
    }

    /**
     * 判断是否有 业务中的订单
     *
     * @param passengerId
     * @return
     */
    private int isPassengerOrderGoingon(Long passengerId) {
        // 判断有正在进行的订单不允许下单
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("passenger_id", passengerId);
        queryWrapper.and(wrapper -> wrapper.eq("order_status", OrderConstants.ORDER_START)
                .or().eq("order_status", OrderConstants.DRIVER_RECEIVE_ORDER)
                .or().eq("order_status", OrderConstants.DRIVER_TO_PICK_UP_PASSENGER)
                .or().eq("order_status", OrderConstants.DRIVER_ARRIVED_DEPARTURE)
                .or().eq("order_status", OrderConstants.PICK_UP_PASSENGER)
                .or().eq("order_status", OrderConstants.PASSENGER_GETOFF)
                .or().eq("order_status", OrderConstants.TO_START_PAY)
        );

        Integer validOrderNumber = orderMapper.selectCount(queryWrapper).intValue();
        return validOrderNumber;
    }

}
