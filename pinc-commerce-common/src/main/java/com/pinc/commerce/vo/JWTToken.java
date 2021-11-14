package com.pinc.commerce.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回的token信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTToken {

    private String token;
}
