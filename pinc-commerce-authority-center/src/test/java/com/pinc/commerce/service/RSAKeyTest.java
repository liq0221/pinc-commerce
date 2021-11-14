package com.pinc.commerce.service;

import cn.hutool.core.codec.Base64;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RSAKeyTest {

    @Test
    public void generatePublicAndPrivateKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        // 生成公钥和私钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // 获取公钥和私钥
        RSAPublicKey rsaPublicKey = (RSAPublicKey)keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

        log.info("public key:[{}]", Base64.encode(rsaPublicKey.getEncoded()));
        log.info("private key:[{}]", Base64.encode(rsaPrivateKey.getEncoded()));

    }

}
