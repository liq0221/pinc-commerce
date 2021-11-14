package com.pinc.commerce.filter;

import com.alibaba.fastjson.JSON;
import com.pinc.commerce.constants.CommonConstants;
import com.pinc.commerce.constants.GatewayConstants;
import com.pinc.commerce.utils.TokenParseUtil;
import com.pinc.commerce.vo.JWTToken;
import com.pinc.commerce.vo.LoginUserInfo;
import com.pinc.commerce.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 登陆，注册，鉴权 过滤器
 */
@Slf4j
@Component
public class GlobalLoginOrRegisterFilter implements GlobalFilter, Ordered {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 如果是登陆 去鉴权中心服务获取token
        if (request.getURI().getPath().contains(GatewayConstants.LOGIN_URI)) {

            String token = getTokenFromAuthorityCenter(
                    request, GatewayConstants.AUTHORITY_CENTER_TOKEN_URL_FORMAT
            );
            response.getHeaders().add(
                    CommonConstants.JWT_USER_INFO_KEY, StringUtils.isNotBlank(token) ? token : "null"
            );
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }

        // 如果是注册 去鉴权中心服务注册用户并获取token
        if (request.getURI().getPath().contains(GatewayConstants.REGISTER_URI)) {

            String token = getTokenFromAuthorityCenter(
                    request, GatewayConstants.AUTHORITY_CENTER_REGISTER_URL_FORMAT
            );
            response.getHeaders().add(
                    CommonConstants.JWT_USER_INFO_KEY, StringUtils.isNotBlank(token) ? token : "null"
            );
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }

        // 如果是访问其他服务 则鉴权
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst(CommonConstants.JWT_USER_INFO_KEY);
        LoginUserInfo loginUserInfo = null;
        try {
            loginUserInfo = TokenParseUtil.parseUserInfoByToken(token);
        } catch (Exception e) {
            log.error("parse token fail [{}]", e.getMessage(), e);
        }

        if (Objects.isNull(loginUserInfo)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 通过 则放行
        return chain.filter(exchange);

    }

    /**
     * 从授权中心获取token
     * @param request
     * @param authorityCenterTokenUrlFormat
     * @return
     */
    private String getTokenFromAuthorityCenter(ServerHttpRequest request, String authorityCenterTokenUrlFormat) {

        ServiceInstance serviceInstance = loadBalancerClient.choose(CommonConstants.AUTHORITY_SERVICE_CENTER_ID);
        log.info("serviceInstance is : [{}]", JSON.toJSONString(serviceInstance.getMetadata()));

        String requestUrl = String.format(authorityCenterTokenUrlFormat, serviceInstance.getHost(), serviceInstance.getPort());
        UsernameAndPassword requestBody = JSON.parseObject(parseBodyFromRequest(request), UsernameAndPassword.class);
        log.info("request url and body: [{}] [{}] ", requestUrl, requestBody);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(JSON.toJSONString(requestBody), headers);

        JWTToken jwtToken = restTemplate.postForObject(requestUrl, entity, JWTToken.class);

        if (!Objects.isNull(jwtToken)) {
            log.info("token is : [{}]", jwtToken.getToken());
            return jwtToken.getToken();
        }
        return null;
    }

    /**
     * 从请求中获取requestBody
     * @param request
     * @return
     */
    private String parseBodyFromRequest(ServerHttpRequest request) {

        // 获取请求体
        Flux<DataBuffer> dataBufferFlux = request.getBody();
        AtomicReference<String> atomicReference = new AtomicReference<>();

        // 订阅缓存区去消费消息体中的数据
        dataBufferFlux.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            // 一定要使用 DataBufferUtils.release 释放掉, 否则, 会出现内存泄露
            DataBufferUtils.release(buffer);
            atomicReference.set(charBuffer.toString());
        });
        return atomicReference.get();
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 2;
    }
}
