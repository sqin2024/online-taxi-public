package com.sqin.apidriver.service;

import com.sqin.apidriver.remote.ServiceDriverUserClient;
import com.sqin.apidriver.remote.ServiceMapClient;
import com.sqin.internalcommon.dto.Car;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.request.ApiDriverPointRequest;
import com.sqin.internalcommon.request.PointRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointService {

    @Autowired
    private ServiceDriverUserClient serviceDriverUserClient;

    @Autowired
    private ServiceMapClient serviceMapClient;

    public ResponseResult upload(ApiDriverPointRequest apiDriverPointRequest) {
        // 获取carId
        Long carId = apiDriverPointRequest.getCarId();

        // 根据carId 获取tid, trid, 调用service-driver-user接口
        ResponseResult<Car> carById = serviceDriverUserClient.getCarById(carId);
        Car car = carById.getData();
        String tid = car.getTid();
        String trid = car.getTrid();

        // 调用地图上传point，调用
        PointRequest pointRequest = new PointRequest();
        pointRequest.setTrid(trid);
        pointRequest.setTid(tid);
        pointRequest.setPoints(apiDriverPointRequest.getPoints());

        return serviceMapClient.upload(pointRequest);
    }

}
