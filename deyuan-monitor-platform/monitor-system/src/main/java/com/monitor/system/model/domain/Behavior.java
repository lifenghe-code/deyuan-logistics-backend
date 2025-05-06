package com.monitor.system.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName illegal_behavior
 */
@TableName(value ="behavior")
@Data
public class Behavior implements Serializable {
    /**
     * Id
     */
    @TableId(type = IdType.AUTO)
    private Long behaviorId;

    /**
     * 行为编码
     */
    private Integer behaviorCode;

    /**
     * 严重等级(1-5)
     */
    private Integer severityLevel;

    /**
     * 行为详细描述
     */
    private String description;

    /**
     * 检测方法 0-机器检测 1-人工检测
     */
    private Integer detectionMethod;

    /**
     * 0-启用检测，1-未启用
     */
    private Integer isActive;

    /**
     * 创建时间
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