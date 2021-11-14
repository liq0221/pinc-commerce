package com.pinc.commerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * gateway相关配置
 */
@Configuration
public class GatewayConfig {

    /** 默认超时时间 */
    public static final int DEFAULT_TIME_OUT = 30000;

    /** Nacos 服务器地址 */
    @Value("${spring.cloud.nacos.discovery.server-addr}")
    public static String NACOS_SERVER_ADDR;

    /** 命名空间 */
    @Value("${spring.cloud.nacos.discovery.namespace}")
    public static String NACOS_NAMESPACE;

    /** data-id */
    @Value("${nacos.gateway.route.config.data-id}")
    public static String NACOS_ROUTE_DATA_ID;

    /** 分组 id */
    @Value("${nacos.gateway.route.config.data-id}")
    public static String NACOS_ROUTE_GROUP;
}
