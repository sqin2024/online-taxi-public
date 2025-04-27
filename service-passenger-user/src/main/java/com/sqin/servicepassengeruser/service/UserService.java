package com.sqin.servicepassengeruser.service;

import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.servicepassengeruser.dto.PassengerUser;
import com.sqin.servicepassengeruser.mapper.PassengerUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private PassengerUserMapper passengerUserMapper;

    public ResponseResult loginOrRegister(String passengerPhone) {

        System.out.println("user service");
        Map<String, Object> map = new HashMap<>();
        map.put("passenger_phone", passengerPhone);
        List<PassengerUser> passengerUsers = passengerUserMapper.selectByMap(map);

        System.out.println(passengerUsers.size() == 0 ? "無記錄" : passengerUsers.get(0).getPassengerPhone());

        return ResponseResult.success();
    }

}
