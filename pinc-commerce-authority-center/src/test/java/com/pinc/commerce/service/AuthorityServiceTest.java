package com.pinc.commerce.service;


import com.oracle.tools.packager.Log;
import com.pinc.commerce.serivce.IJWTService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class AuthorityServiceTest {

    @Autowired
    private IJWTService ijwtService;


    @Test
    public void test() throws Exception{
        log.info(ijwtService.generateToken("zhangsan", "e10adc3949ba59abbe56e057f20f883e"));
    }
}
