package com.sqin.apipassenger.controller;

import com.sqin.apipassenger.request.VerificationCodeDTO;
import com.sqin.apipassenger.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationCodeController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    @GetMapping("/verification-code")
    public String verificationCode(@RequestBody VerificationCodeDTO verificationCodeDTO) {

        System.out.println(verificationCodeDTO.getPassengerPhone());

        return verificationCodeService.generatorCode(verificationCodeDTO.getPassengerPhone());
    }
}
