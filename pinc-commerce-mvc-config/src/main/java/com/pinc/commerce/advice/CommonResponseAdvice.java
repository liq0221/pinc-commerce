package com.pinc.commerce.advice;

import com.pinc.commerce.annotation.IgnoreCommonResponseAdvice;
import com.pinc.commerce.vo.CommonResponseVO;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

/**
 * 处理返回信息
 */
@RestControllerAdvice("com.pinc.commerce")
public class CommonResponseAdvice implements ResponseBodyAdvice {

    @Override
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter, Class aClass) {

        if (methodParameter.getDeclaringClass()
                .isAnnotationPresent(IgnoreCommonResponseAdvice.class)) {
            return false;
        }

        if (methodParameter.getMethod()
                .isAnnotationPresent(IgnoreCommonResponseAdvice.class)) {
            return false;
        }

        return true;
    }

    @Override
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object o
            , MethodParameter methodParameter
            , MediaType mediaType
            , Class aClass
            , ServerHttpRequest serverHttpRequest
            , ServerHttpResponse serverHttpResponse) {

        // 定义返回信息
        CommonResponseVO<Object> response = new CommonResponseVO<>(0, "");

        if (Objects.isNull(o)) {
            return response;
        } else if (o instanceof CommonResponseVO) {
            response = (CommonResponseVO<Object>) o;
        } else {
            response.setData(o);
        }
        return response;
    }
}
