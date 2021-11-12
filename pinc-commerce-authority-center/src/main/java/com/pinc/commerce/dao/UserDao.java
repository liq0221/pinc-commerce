package com.pinc.commerce.dao;

import com.pinc.commerce.entity.CommerceUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserDao extends JpaRepository<CommerceUser, Long> {

    /**
     * <h2>根据用户名查询 EcommerceUser 对象</h2>
     * select * from t_ecommerce_user where username = ?
     * */
    CommerceUser findByUsername(String username);

    /**
     * <h2>根据用户名和密码查询实体对象</h2>
     * select * from t_ecommerce_user where username = ? and password = ?
     * */
    CommerceUser findByUsernameAndPassword(String username, String password);
}
