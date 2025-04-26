package com.sqin.apipassenger.service;

import com.sqin.apipassenger.remote.ServiceVerificationCodeClient;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.response.NumberCodeResponse;
import net.sf.json.JSONObject;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ServiceVerificationCodeClient serviceVerificationCodeClient;

    private String verificationCodePrefix = "passenger-verification-code-";

    public String generatorCode(String passengerPhone) {
        //调用验证码服务
        System.out.println("获取验证码");
        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerificationCodeClient.getNumberCode(6);
        int numberCode = numberCodeResponse.getData().getNumberCode();

        System.out.println("remote number code: " + numberCode);

        System.out.println("save to redis");
        // key value expire time
        String key = verificationCodePrefix + passengerPhone;
        stringRedisTemplate.opsForValue().set(key, numberCode + "", 2, TimeUnit.MINUTES);

        JSONObject result = new JSONObject();
        result.put("code", 1);
        result.put("message", "success 26");
        return result.toString();
    }
}
