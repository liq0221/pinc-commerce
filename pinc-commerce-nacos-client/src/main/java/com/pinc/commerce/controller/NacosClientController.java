package com.pinc.commerce.controller;

import com.pinc.commerce.config.ProjectConfig;
import com.pinc.commerce.service.NacosClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 服务列表
 */
@Slf4j
@RestController
@RequestMapping("/nacos-client")
public class NacosClientController {

    @Autowired
    private NacosClientService nacosClientService;

    @Resource
    private ProjectConfig projectConfig;

    @GetMapping("/service-instance")
    public List<ServiceInstance> outputServiceList(@RequestParam(defaultValue = "pinc-commerce-nacos-client") String serviceId) {
        log.info("coming in log nacos client info: [{}]", serviceId);
        return nacosClientService.outputServiceList(serviceId);
    }

    @GetMapping("/project-config")
    public ProjectConfig outputConfig() {
        return projectConfig;
    }
}
