package com.monitor.system.model.dto.admin;

import lombok.Data;

import java.io.Serializable;
@Data
public class LoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 登录账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 验证码
     */


}
