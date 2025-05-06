package com.monitor.system.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName staff
 */
@TableName(value ="staff")
@Data
public class Staff implements Serializable {
    /**
     * Id
     */
    @TableId(type = IdType.AUTO)
    private Long staffId;

    /**
     * 登录用户名
     */
    private String staffName;

    /**
     * 员工类型
     */
    private Integer staffType;

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 车牌号
     */
    private String plateNumber;


    /**
     * 员工等级
     */
    private Integer grade;


    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 0-未知 1-男 2-女
     */
    private Integer gender;

    /**
     * 出生日期
     */
    private Date birthDate;

    /**
     * 0-启用 1-禁用 2-未激活
     */
    private Integer status;

    /**
     * 注册时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private Integer delFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}