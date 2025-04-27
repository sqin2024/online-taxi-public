package com.sqin.apipassenger.controller;

import com.sqin.internalcommon.request.VerificationCodeDTO;
import com.sqin.apipassenger.service.VerificationCodeService;
import com.sqin.internalcommon.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationCodeController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    /**
     * 用戶輸入手機號后，獲取驗證碼
     * @param verificationCodeDTO
     * @return
     */
    @GetMapping("/verification-code")
    public ResponseResult verificationCode(@RequestBody VerificationCodeDTO verificationCodeDTO) {
        return verificationCodeService.generatorCode(verificationCodeDTO.getPassengerPhone());
    }

    @PostMapping("/verification-code-check")
    public ResponseResult checkVerificationCode(@RequestBody VerificationCodeDTO verificationCodeDTO) {

        String passengerPhone = verificationCodeDTO.getPassengerPhone();
        String verificationCode = verificationCodeDTO.getVerificationCode();

        System.out.println("passengerPhone : "+ passengerPhone + ", verificationCode :" + verificationCode);

        return verificationCodeService.checkCode(passengerPhone, verificationCode);
    }
}
