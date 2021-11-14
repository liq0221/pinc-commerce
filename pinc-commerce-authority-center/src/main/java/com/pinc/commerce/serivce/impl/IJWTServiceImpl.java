package com.pinc.commerce.serivce.impl;

import com.alibaba.fastjson.JSON;
import com.pinc.commerce.constants.AuthorityConstants;
import com.pinc.commerce.constants.CommonConstants;
import com.pinc.commerce.dao.UserDao;
import com.pinc.commerce.entity.CommerceUser;
import com.pinc.commerce.serivce.IJWTService;
import com.pinc.commerce.vo.LoginUserInfo;
import com.pinc.commerce.vo.UsernameAndPassword;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * jwt实现类
 */
@Service
@Slf4j
public class IJWTServiceImpl implements IJWTService {

    @Autowired
    private UserDao userDao;

    @Override
    public String generateToken(String username, String password) throws Exception {
        return generateToken(username, password, 0);
    }

    @Override
    public String generateToken(String username, String password, int expire) throws Exception {

        // 首先判断数据库是否保存该用户名和密码
        CommerceUser commerceUser = userDao.findByUsername(username);
        if (Objects.isNull(commerceUser)) {
           log.error("[{}] user not exists", username);
           return null;
        }

        if (0 >= expire) {
            expire = AuthorityConstants.DEFAULT_EXPIRE_DATE;
        }

        LoginUserInfo loginUserInfo = new LoginUserInfo(commerceUser.getId(), username);

        // 计算超时时间
        ZonedDateTime zonedDateTime = LocalDate.now().plus(expire, ChronoUnit.DAYS)
                .atStartOfDay(ZoneId.systemDefault());
        Date date = Date.from(zonedDateTime.toInstant());


        return Jwts.builder()
                .claim(CommonConstants.JWT_USER_INFO_KEY, JSON.toJSONString(loginUserInfo))
                .setExpiration(date)
                .setId(UUID.randomUUID().toString())
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    @Override
    public String registerUserAndGenerateToken(UsernameAndPassword usernameAndPassword) throws Exception {

        CommerceUser commerceUser = userDao.findByUsername(usernameAndPassword.getUsername());
        if (!Objects.isNull(commerceUser)) {
            log.error("[{}] username is exists", usernameAndPassword.getUsername());
            return null;
        }

        // 保存用户
        CommerceUser saveCommerceUser = new CommerceUser();
        BeanUtils.copyProperties(usernameAndPassword, saveCommerceUser);
        saveCommerceUser.setExtraInfo("{}");
        saveCommerceUser = userDao.save(saveCommerceUser);
        log.info("save user success [{}] , [{}]", saveCommerceUser.getUsername()
                , saveCommerceUser.getPassword());

        return generateToken(saveCommerceUser.getUsername(), saveCommerceUser.getPassword());
    }

    private PrivateKey getPrivateKey() throws Exception{
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                new BASE64Decoder().decodeBuffer(AuthorityConstants.PRIVATE_KEY)
        );
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }
}
