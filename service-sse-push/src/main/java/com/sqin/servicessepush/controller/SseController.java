package com.sqin.servicessepush.controller;

import com.sqin.internalcommon.request.PushRequest;
import com.sqin.internalcommon.util.SsePrefixUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class SseController {

    public static Map<String, SseEmitter> sseEmitterMap = new HashMap<>();


    /**
     * 建立连接
     *
     * @param userId
     * @param identity
     * @return
     */
    @GetMapping("/connect")
    public SseEmitter connect(@RequestParam Long userId, @RequestParam String identity) {
        log.info("userID " + userId + ", identity: " + identity);
        SseEmitter sseEmitter = new SseEmitter(0l);

        String sseMapKey = SsePrefixUtils.generateSseKey(userId, identity);
        sseEmitterMap.put(sseMapKey, sseEmitter);
        return sseEmitter;
    }

    /**
     * 发送消息
     *
     * @param pushRequest
     * @return
     */
    @PostMapping("/push")
    public String push(@RequestBody PushRequest pushRequest) {
        String sseMapKey = SsePrefixUtils.generateSseKey(pushRequest.getUserId(), pushRequest.getIdentity());
        try {
            if (sseEmitterMap.containsKey(sseMapKey)) {
                sseEmitterMap.get(sseMapKey).send(pushRequest.getContent());
            } else {
                return "推送失败";
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "给用户" + sseMapKey + ", 发送了消息:" + pushRequest.getContent();
    }

    @GetMapping("/close")
    public String close(@RequestParam Long userId, @RequestParam String identity) {
        String sseMapKey = SsePrefixUtils.generateSseKey(userId, identity);
        System.out.println("close connect " + sseMapKey);
        if (sseEmitterMap.containsKey(sseMapKey)) {
            sseEmitterMap.remove(sseMapKey);
        }
        return "closed";
    }

}
