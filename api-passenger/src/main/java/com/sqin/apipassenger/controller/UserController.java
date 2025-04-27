package com.sqin.apipassenger.controller;

import com.sqin.apipassenger.service.UserService;
import com.sqin.internalcommon.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseResult getUser(HttpServletRequest request) {
        // get accessToken
        return userService.getUserByAccessToken(request.getHeader("Authorization"));
    }

}
