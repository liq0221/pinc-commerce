package com.pinc.commerce.config;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 通过nacos下发路由配置  监听nacos路由变化
 */
@Service
@Slf4j
@DependsOn({"gatewayConfig"})
public class DynamicRouteServiceImplByNacos {

    @Autowired
    private DynamicRouteServiceImpl dynamicRouteService;

    private ConfigService configService;

    @PostConstruct
    public void init() {
        log.info("gateway route init....");

        ConfigService configService = initConfigService();
        if (Objects.isNull(configService)) {
            log.error("init fail");
            return;
        }

        try {
            String config = configService.getConfig(
                    GatewayConfig.NACOS_ROUTE_DATA_ID,
                    GatewayConfig.NACOS_ROUTE_GROUP,
                    GatewayConfig.DEFAULT_TIME_OUT
            );

            log.info("get current gateway config: [{}]", config);
            List<RouteDefinition> routeDefinitions = JSON.parseArray(config, RouteDefinition.class);
            if (CollectionUtil.isNotEmpty(routeDefinitions)) {
                routeDefinitions.stream().forEach(routeDefinition -> {
                    log.info("init gateway config: [{}]", routeDefinition.toString());
                    dynamicRouteService.addRoute(routeDefinition);
                });
            }
        } catch (Exception e) {
            log.info("init gateway config fail: [{}]", e.getMessage(), e);
        }
        dynamicListner(GatewayConfig.NACOS_ROUTE_DATA_ID, GatewayConfig.NACOS_ROUTE_GROUP);
    }

    /**
     * 监听nacos配置变化
     * @param nacosRouteDataId
     * @param nacosRouteGroup
     */
    private void dynamicListner(String nacosRouteDataId, String nacosRouteGroup) {

        try {
            configService.addListener(nacosRouteDataId, nacosRouteGroup, new Listener() {

                @Override
                public Executor getExecutor() {
                    return null;
                }

                /**
                 * 监听器收到配置更新
                 * @param config
                 */
                @Override
                public void receiveConfigInfo(String config) {

                    log.info("update route [{}]", config);
                    List<RouteDefinition> routeDefinitions = JSON.parseArray(config, RouteDefinition.class);
                    log.info("update route: [{}]", routeDefinitions.toString());
                    dynamicRouteService.updateList(routeDefinitions);
                }
            });
        } catch (NacosException e) {
            log.error("update route fail [{}]", e.getMessage(), e);
        }

    }

    private ConfigService initConfigService() {
        try {

            Properties properties = new Properties();
            properties.setProperty("serverAddr", GatewayConfig.NACOS_SERVER_ADDR);
            properties.setProperty("namespace", GatewayConfig.NACOS_NAMESPACE);
            return configService = NacosFactory.createConfigService(properties);
        } catch (Exception e) {
            log.error("initConfigService fail [{}]", e.getMessage(), e);
            return null;
        }
    }
}
