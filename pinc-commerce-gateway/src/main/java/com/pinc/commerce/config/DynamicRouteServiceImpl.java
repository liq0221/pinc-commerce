package com.pinc.commerce.config;


import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 动态网关service
 */
@Service
@Slf4j
public class DynamicRouteServiceImpl implements ApplicationEventPublisherAware {

    /** 写路由定义 */
    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;
    /** 获取路由 */
    @Autowired
    private RouteDefinitionLocator routeDefinitionLocator;
    /** 发布事件 */
    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    /**
     * 增加路由
     */
    public String addRoute(RouteDefinition definition) {

        log.info("gateway add route:[{}]", definition);

        // 保存路由并发布
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        // 发布事件通知gateway
        this.publisher.publishEvent(new RefreshRoutesEvent(this));

        return "success";
    }

    public String updateList(List<RouteDefinition> routeDefinitionList) {

        log.info("gateway update route: [{}]", routeDefinitionList);

        List<RouteDefinition> routeDefinitions =
                routeDefinitionLocator.getRouteDefinitions().buffer().blockFirst();
        if (CollectionUtil.isNotEmpty(routeDefinitions)) {
            // 清除之前所有的route
            routeDefinitions.forEach(item -> {
                log.info("delete route by id [{}]", item.getId());
                deleteById(item.getId());
            });
        }

        routeDefinitions.forEach(item -> update(item));
        return "success";
    }

    /**
     * <h2>根据路由 id 删除路由配置</h2>
     * */
    private String deleteById(String id) {

        try {
            log.info("gateway delete route id: [{}]", id);
            this.routeDefinitionWriter.delete(Mono.just(id)).subscribe();
            // 发布事件通知给 gateway 更新路由定义
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            return "delete success";
        } catch (Exception ex) {
            log.error("gateway delete route fail: [{}]", ex.getMessage(), ex);
            return "delete fail";
        }
    }

    /**
     * <h2>更新路由</h2>
     * 更新的实现策略比较简单: 删除 + 新增 = 更新
     * */
    private String update(RouteDefinition definition) {

        try {
            log.info("gateway update route: [{}]", definition);
            this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
        } catch (Exception ex) {
            return "update fail, not find route routeId: " + definition.getId();
        }

        try {
            this.routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            return "success";
        } catch (Exception ex) {
            return "update route fail";
        }
    }

}
