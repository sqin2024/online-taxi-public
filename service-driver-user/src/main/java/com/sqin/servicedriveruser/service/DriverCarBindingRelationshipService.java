package com.sqin.servicedriveruser.service;

import com.sqin.internalcommon.constant.DriverCarConstants;
import com.sqin.internalcommon.dto.DriverCarBindingRelationship;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.servicedriveruser.mapper.DriverCarBindingRelationshipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务类
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

        LocalDateTime now = LocalDateTime.now();
        driverCarBindingRelationship.setBindingTime(now);
        driverCarBindingRelationship.setBindState(DriverCarConstants.DRIVER_CAR_BIND);
        int insert = driverCarBindingRelationshipMapper.insert(driverCarBindingRelationship);
        return ResponseResult.success(insert);
    }

    public ResponseResult unbind(DriverCarBindingRelationship driverCarBindingRelationship) {
        LocalDateTime now = LocalDateTime.now();
        driverCarBindingRelationship.setUnBindingTime(now);
        driverCarBindingRelationship.setBindState(DriverCarConstants.DRIVER_CAR_UNBIND);
        int insert = driverCarBindingRelationshipMapper.insert(driverCarBindingRelationship);
        return ResponseResult.success(insert);
    }
}
