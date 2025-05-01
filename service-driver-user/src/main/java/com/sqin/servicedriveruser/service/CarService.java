package com.sqin.servicedriveruser.service;

import com.sqin.internalcommon.dto.Car;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.response.TerminalResponse;
import com.sqin.internalcommon.response.TrackResponse;
import com.sqin.servicedriveruser.mapper.CarMapper;
import com.sqin.servicedriveruser.remote.ServiceMapClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CarService {

    @Autowired
    private CarMapper carMapper;

    @Autowired
    private ServiceMapClient serviceMapClient;

    public ResponseResult addCar(Car car) {
        LocalDateTime now = LocalDateTime.now();
        car.setGmtCreate(now);
        car.setGmtModified(now);
        carMapper.insert(car);

        /**
         * 创建终端
         */
        ResponseResult<TerminalResponse> responseResult = serviceMapClient.addTerminal(car.getVehicleNo(), car.getId() + "");
        String tid = responseResult.getData().getTid();
        car.setTid(tid);

        /**
         * 初始化轨迹
         */
        ResponseResult<TrackResponse> responseResponseResult = serviceMapClient.addTrack(tid);
        TrackResponse data = responseResponseResult.getData();
        car.setTrid(data.getTrid());
        car.setTrname(data.getTrname());

        carMapper.updateById(car);

        return ResponseResult.success("1");
    }

    public ResponseResult<Car> getCarById(Long carId) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", carId);
        List<Car> cars = carMapper.selectByMap(map);
        return ResponseResult.success(cars.get(0));
    }

}
