package com.pinc.commerce.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用响应返回vo
 * @param <T>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponseVO<T> {

    // 错误码
    private Integer code;

    // 错误信息
    private String message;

    // 返回数据
    private T data;

    public CommonResponseVO(int code, String msg) {
        this.code = code;

        this.message = msg;
    }
}
