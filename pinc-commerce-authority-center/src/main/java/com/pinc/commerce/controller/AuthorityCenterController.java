package com.pinc.commerce.controller;

import com.alibaba.fastjson.JSON;
import com.pinc.commerce.annotation.IgnoreCommonResponseAdvice;
import com.pinc.commerce.serivce.IJWTService;
import com.pinc.commerce.vo.JWTToken;
import com.pinc.commerce.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户授权controller
 */
@RestController
@Slf4j
@RequestMapping("/authority")
public class AuthorityCenterController {

    @Autowired
    private IJWTService ijwtService;

    @IgnoreCommonResponseAdvice
    @RequestMapping("/token")
    public JWTToken token(@RequestBody UsernameAndPassword usernameAndPassword) throws Exception{
        log.info("request to get token with param: [{}]",
                JSON.toJSONString(usernameAndPassword));
        return new JWTToken(ijwtService.generateToken(
                usernameAndPassword.getUsername(),
                usernameAndPassword.getPassword()
        ));
    }

    @RequestMapping("/register")
    @IgnoreCommonResponseAdvice
    public JWTToken register(@RequestBody UsernameAndPassword usernameAndPassword) throws  Exception {


        log.info("register user with param: [{}]", JSON.toJSONString(
                usernameAndPassword
        ));
        return new JWTToken(ijwtService.registerUserAndGenerateToken(
                usernameAndPassword
        ));
    }
}
