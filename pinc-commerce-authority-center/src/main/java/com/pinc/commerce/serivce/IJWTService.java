package com.pinc.commerce.serivce;

import com.pinc.commerce.vo.UsernameAndPassword;

/**
 * 授权service
 */
public interface IJWTService {

    /**
     * 生成默认超时时间的token
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    String generateToken(String username, String password) throws Exception;

    /**
     * 生成指定超时时间的token
     * @param username
     * @param password
     * @param expire
     * @return
     * @throws Exception
     */
    String generateToken(String username, String password, int expire) throws Exception;

    /**
     * 注册完成并返回token
     * @param usernameAndPassword
     * @return
     * @throws Exception
     */
    String registerUserAndGenerateToken(UsernameAndPassword usernameAndPassword) throws Exception;
}
