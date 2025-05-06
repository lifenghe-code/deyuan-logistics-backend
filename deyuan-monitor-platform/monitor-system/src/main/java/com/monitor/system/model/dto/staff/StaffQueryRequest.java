package com.monitor.system.model.dto.staff;

import com.monitor.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class StaffQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long staffId;

    /**
     * 登录用户名
     */
    private String staffName;

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
     * 员工类型
     */
    private Integer staffType;

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 员工等级
     */
    private Integer grade;


}
