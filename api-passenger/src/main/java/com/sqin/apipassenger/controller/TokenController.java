package com.sqin.apipassenger.controller;

import com.sqin.apipassenger.service.TokenService;
import com.sqin.internalcommon.dto.ResponseResult;
import com.sqin.internalcommon.response.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @PostMapping("/token-refresh")
    public ResponseResult refreshToken(@RequestBody TokenResponse tokenResponse) {
        String refreshToken = tokenResponse.getRefreshToken();

        return tokenService.refreshToken(refreshToken);
    }

}
