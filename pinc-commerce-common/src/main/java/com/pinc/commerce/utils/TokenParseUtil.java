package com.pinc.commerce.utils;

import com.alibaba.fastjson.JSON;
import com.pinc.commerce.constants.CommonConstants;
import com.pinc.commerce.vo.LoginUserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import sun.misc.BASE64Decoder;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Objects;

public class TokenParseUtil {

    public static LoginUserInfo  parseUserInfoByToken(String token) throws Exception{

        if (Objects.isNull(token)) {
            return null;
        }

        Jws<Claims> claims = getClaims(token, getPublicKey());
        Claims body = claims.getBody();

        if (body.getExpiration().before(Calendar.getInstance().getTime())) {
            return null;
        }

        return JSON.parseObject(body.get(CommonConstants.JWT_USER_INFO_KEY).toString(), LoginUserInfo.class);
    }


    private static Jws<Claims> getClaims(String token, PublicKey publicKey) {

        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
    }


    private static PublicKey getPublicKey() throws Exception{

        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
                new BASE64Decoder().decodeBuffer(CommonConstants.PUBLIC_KEY)
        );
        return KeyFactory.getInstance("RSA").generatePublic(x509EncodedKeySpec);
    }
}
