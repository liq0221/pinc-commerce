package com.pinc.commerce.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NacosClientService {

    @Autowired
    private DiscoveryClient discoveryClient;

    public List<ServiceInstance> outputServiceList(String serviceId) {
        log.info("out put service list to nacos, serviceId : [{}]", serviceId);
        return discoveryClient.getInstances(serviceId);
    }
}
