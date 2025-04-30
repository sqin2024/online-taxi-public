package com.sqin.servicedriveruser.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sqin.internalcommon.constant.CommonStatusEnum;
import com.sqin.internalcommon.constant.DriverCarConstants;
import com.sqin.internalcommon.dto.DriverCarBindingRelationship;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.servicedriveruser.mapper.DriverCarBindingRelationshipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Driver;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Qin
 * @since 2025-04-30
 */
@Service
public class DriverCarBindingRelationshipService {

    @Autowired
    private DriverCarBindingRelationshipMapper driverCarBindingRelationshipMapper;

    public ResponseResult bind(DriverCarBindingRelationship driverCarBindingRelationship) {
        // 判断当前绑定关系
        QueryWrapper<DriverCarBindingRelationship> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("driver_id", driverCarBindingRelationship.getDriverId());
        queryWrapper.eq("car_id", driverCarBindingRelationship.getCarId());
        queryWrapper.eq("bind_state", DriverCarConstants.DRIVER_CAR_BIND);
        Long count = driverCarBindingRelationshipMapper.selectCount(queryWrapper);
        if (count.intValue() > 0) {
            return ResponseResult.fail(CommonStatusEnum.DRIVER_CAR_BIND_EXIST.getCode(), CommonStatusEnum.DRIVER_CAR_BIND_EXIST.getValue());
        }
        // 车辆已经被绑定了
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("driver_id", driverCarBindingRelationship.getDriverId());
        queryWrapper.eq("bind_state", DriverCarConstants.DRIVER_CAR_BIND);
        count = driverCarBindingRelationshipMapper.selectCount(queryWrapper);
        if (count.intValue() > 0) {
            return ResponseResult.fail(CommonStatusEnum.DRIVER_BIND_EXIST.getCode(), CommonStatusEnum.DRIVER_BIND_EXIST.getValue());
        }

        // 司机已绑定其他车辆
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("car_id", driverCarBindingRelationship.getCarId());
        queryWrapper.eq("bind_state", DriverCarConstants.DRIVER_CAR_BIND);
        count = driverCarBindingRelationshipMapper.selectCount(queryWrapper);
        if (count.intValue() > 0) {
            return ResponseResult.fail(CommonStatusEnum.CAR_BIND_EXIST.getCode(), CommonStatusEnum.CAR_BIND_EXIST.getValue());
        }

        LocalDateTime now = LocalDateTime.now();
        driverCarBindingRelationship.setBindingTime(now);
        driverCarBindingRelationship.setBindState(DriverCarConstants.DRIVER_CAR_BIND);
        int insert = driverCarBindingRelationshipMapper.insert(driverCarBindingRelationship);
        return ResponseResult.success(insert);
    }

    public ResponseResult unbind(DriverCarBindingRelationship driverCarBindingRelationship) {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> map = new HashMap<>();
        map.put("driver_id", driverCarBindingRelationship.getDriverId());
        map.put("car_id", driverCarBindingRelationship.getCarId());
        map.put("bind_state", DriverCarConstants.DRIVER_CAR_BIND);
        List<DriverCarBindingRelationship> driverCarBindingRelationships = driverCarBindingRelationshipMapper.selectByMap(map);
        if (driverCarBindingRelationships.isEmpty()) {
            return ResponseResult.fail(CommonStatusEnum.DRIVER_CAR_BIND_NOT_EXIST.getCode(), CommonStatusEnum.DRIVER_CAR_BIND_NOT_EXIST.getValue());
        }

        DriverCarBindingRelationship relationship = driverCarBindingRelationships.get(0);
        relationship.setBindState(DriverCarConstants.DRIVER_CAR_UNBIND);
        relationship.setUnBindingTime(now);
        int i = driverCarBindingRelationshipMapper.updateById(relationship);
        return ResponseResult.success(i);
    }
}
