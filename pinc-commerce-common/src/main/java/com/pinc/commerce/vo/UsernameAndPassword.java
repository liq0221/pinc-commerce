package com.pinc.commerce.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户名和密码
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsernameAndPassword {

    private String username;

    private String password;
}
