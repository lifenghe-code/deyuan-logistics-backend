package com.monitor.system.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AdminVo implements Serializable {
    /**
     * Id
     */

    private Long adminId;

    /**
     * 登录用户名
     */
    private String adminName;

    /**
     * 登录账号
     */
    private String account;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 0-启用 1-禁用 2-未激活
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;


    private static final long serialVersionUID = 1L;
}
