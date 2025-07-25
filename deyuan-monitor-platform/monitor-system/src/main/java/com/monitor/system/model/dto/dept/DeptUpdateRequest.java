package com.monitor.system.model.dto.dept;

import lombok.Data;

@Data
public class DeptUpdateRequest {
    private static final long serialVersionUID = 1L;
    /**
     * Id
     */
    private Long deptId;
    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门描述
     */
    private String deptDescription;

    /**
     * 负责人ID
     */
    private Long leaderId;

    /**
     * 负责人姓名
     */
    private String leaderName;

    /**
     * 0-启用 1-禁用 2-未激活
     */
    private Integer status;
}
