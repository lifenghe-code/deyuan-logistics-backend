package com.monitor.system.model.dto.staff;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class StaffAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;

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
}
