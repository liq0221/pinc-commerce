package com.pinc.commerce.constants;

/**
 * 网关通用constants
 */
public class GatewayConstants {

    /** 登录 uri */
    public static final String LOGIN_URI = "/pinc-commerce/login";

    /** 注册 uri */
    public static final String REGISTER_URI = "/pinc-commerce/register";

    /** 去授权中心拿到登录 token 的 uri 格式化接口 */
    public static final String AUTHORITY_CENTER_TOKEN_URL_FORMAT =
            "http://%s:%s/pinc-commerce-authority-center/authority/token";

    /** 去授权中心注册并拿到 token 的 uri 格式化接口 */
    public static final String AUTHORITY_CENTER_REGISTER_URL_FORMAT =
            "http://%s:%s/pinc-commerce-authority-center/authority/register";
}
