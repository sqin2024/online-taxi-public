package com.sqin.serviceorder.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sqin.internalcommon.constant.CommonStatusEnum;
import com.sqin.internalcommon.constant.OrderConstants;
import com.sqin.internalcommon.dto.OrderInfo;
import com.sqin.internalcommon.dto.PriceRule;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.OrderRequest;
import com.sqin.internalcommon.request.PriceRuleIsNewRequest;
import com.sqin.internalcommon.response.OrderDriverResponse;
import com.sqin.internalcommon.response.TerminalResponse;
import com.sqin.internalcommon.util.RedisPrefixUtils;
import com.sqin.serviceorder.mapper.OrderMapper;
import com.sqin.serviceorder.remote.ServiceDriverUserClient;
import com.sqin.serviceorder.remote.ServiceMapClient;
import com.sqin.serviceorder.remote.ServicePriceClient;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ServiceMapClient serviceMapClient;

    @Autowired
    private ServicePriceClient servicePriceClient;

    @Autowired
    private ServiceDriverUserClient serviceDriverUserClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    public ResponseResult add(OrderRequest orderRequest) {

        /**
         * 查看当前城市是否有司机
         */
        ResponseResult<Boolean> availableDriver = serviceDriverUserClient.isAvailableDriver(orderRequest.getAddress());
        if (!availableDriver.getData()) {
            return ResponseResult.fail(CommonStatusEnum.CITY_DRIVER_EMPTY.getCode(), CommonStatusEnum.CITY_DRIVER_EMPTY.getValue());
        }

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
         * 判断乘客是否有正在进行的订单
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

        /**
         * 创建订单
         */
        orderMapper.insert(orderInfo);

        /**
         * 派单
         */
        dispatchRealTimeOrder(orderInfo);
        return ResponseResult.success();
    }

    /**
     * 实时订单派单逻辑
     *
     * 单机情况下，解决派单的并发问题
     *
     * @param orderInfo
     */
    public void dispatchRealTimeOrder(OrderInfo orderInfo) {
        // 2km
        String depLongitude = orderInfo.getDepLongitude();
        String depLatitude = orderInfo.getDepLatitude();
        String center = depLatitude + "," + depLongitude;

        List<Integer> radiusList = new ArrayList<>();
        radiusList.add(2000);
        radiusList.add(4000);
        radiusList.add(5000);

        ResponseResult<List<TerminalResponse>> listResponseResult = null;
        for (int i = 0; i < radiusList.size(); i++) {
            Integer radius = radiusList.get(i);
            listResponseResult = serviceMapClient.aroundSearch(center, radius);
            log.info("寻找车辆" + radius);
            log.info("找到车辆了，数量为：" + listResponseResult.getData().size());
            // 获得终端  "tid": 1276359444, "name": "0y580", "desc": "1918494243060858881",

            // 解析终端
            List<TerminalResponse> data = listResponseResult.getData();
            // goto语法为了测试
            radius:
            for (int j = 0; j < data.size(); j++) {
                TerminalResponse terminalResponse = data.get(j);
                Long carId = terminalResponse.getCarId();

                String longitude = terminalResponse.getLongitude();
                String latitude = terminalResponse.getLatitude();

                ResponseResult<OrderDriverResponse> availableDriver = serviceDriverUserClient.getAvailableDriver(carId);
                if (availableDriver.getCode() == CommonStatusEnum.AVAILABLE_DRIVER_EMPTY.getCode()) {
                    continue radius;
                } else {
                    log.info("找到了正在出车的司机，车辆id是：" + carId);
                    OrderDriverResponse orderDriverResponse = availableDriver.getData();
                    Long driverId = orderDriverResponse.getDriverId();
                    String driverPhone = orderDriverResponse.getDriverPhone();
                    String licenseId = orderDriverResponse.getLicenseId();
                    String vehicleNo = orderDriverResponse.getVehicleNo();
                    String vehicleTypeFromCar = orderDriverResponse.getVehicleType();

                    String lockKey = (driverId + "").intern();
                    RLock lock = redissonClient.getLock(lockKey);
                    lock.lock();

                    /**
                     * 判断乘客是否有正在进行的订单
                     */
                    if (isDriverOrderGoingon(driverId) > 0) {
                        lock.unlock();
                        continue ;
                    }
                    // 直接给司机派单
                    log.info("车辆ID："+carId+"找到了正在出车的司机");

                    orderInfo.setDriverId(driverId);
                    orderInfo.setDriverPhone(driverPhone);
                    orderInfo.setCarId(carId);

                    orderInfo.setDepLongitude(longitude);
                    orderInfo.setDepLatitude(latitude);

                    orderInfo.setReceiveOrderTime(LocalDateTime.now());
                    orderInfo.setLicenseId(licenseId);
                    orderInfo.setVehicleNo(vehicleNo);
                    orderInfo.setVehicleType(vehicleTypeFromCar);
                    orderInfo.setOrderStatus(OrderConstants.DRIVER_RECEIVE_ORDER);

                    orderMapper.updateById(orderInfo);
                    lock.unlock();

                    break radius;
                }

            }

            // 根据解析出来的终端，查询车辆信息

            // 找到符合的车辆，进行派单

            // 如果派单成功，退出循环
        }


//        List<TerminalResponse> data = listResponseResult.getData();
//        if(data.size() == 0) {
//            radius = radius + 2000;
//            listResponseResult = serviceMapClient.aroundSearch(center, radius);
//            if(data.size() == 0) {
//                radius = radius + 1000;
//                listResponseResult = serviceMapClient.aroundSearch(center, radius);
//                if(data.size() == 0) {
//                    log.info("这轮没找到车");
//                }
//            }
//        }

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

    /**
     * 判断是否有 业务中的订单
     * @param driverId
     * @return
     */
    private int isDriverOrderGoingon(Long driverId){
        // 判断有正在进行的订单不允许下单
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("driver_id",driverId);
        queryWrapper.and(wrapper->wrapper
                .eq("order_status",OrderConstants.DRIVER_RECEIVE_ORDER)
                .or().eq("order_status",OrderConstants.DRIVER_TO_PICK_UP_PASSENGER)
                .or().eq("order_status",OrderConstants.DRIVER_ARRIVED_DEPARTURE)
                .or().eq("order_status",OrderConstants.PICK_UP_PASSENGER)

        );
        Long validOrderNumber = orderMapper.selectCount(queryWrapper);
        log.info("司机Id："+driverId+",正在进行的订单的数量："+validOrderNumber);
        return validOrderNumber.intValue();
    }

}
