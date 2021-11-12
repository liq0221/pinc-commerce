package com.pinc.commerce.service;

import cn.hutool.crypto.digest.MD5;
import com.pinc.commerce.dao.UserDao;
import com.pinc.commerce.entity.CommerceUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class UserServiceTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void save() {
        CommerceUser commerceUser = new CommerceUser();
        commerceUser.setUsername("zhangsan");
        commerceUser.setPassword(MD5.create().digestHex("123456"));
        commerceUser.setExtraInfo("testestest");
        log.info("save user:[{}]", userDao.save(commerceUser));
    }
}
