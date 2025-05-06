package com.monitor.system.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName record
 */
@TableName(value ="record")
@Data
public class Record implements Serializable {
    /**
     * Id
     */
    @TableId(type = IdType.AUTO)
    private Long recordId;

    /**
     * 司机ID
     */
    private Long driverId;

    /**
     * 押运员ID
     */
    private Long copilotId;

    /**
     * 驾驶员姓名
     */
    private String driverName;


    /**
     * 押运员姓名
     */
    private String copilotName;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 关联的违规行为类型ID
     */
    private Long behaviorId;

    /**
     * 行为编码
     */
    private Integer behaviorCode;

    /**
     * 违规发生时间
     */
    private Date occurrenceTime;

    /**
     * 发现时间
     */
    private Date discoveryTime;

    /**
     * 发现方式(系统检测/人工举报等)
     */
    private Integer discoveryMethod;

    /**
     * 违规行为详细描述
     */
    private String description;

    /**
     * 证据材料(图片/视频/日志等)
     */
    private String evidence;

    /**
     * 严重等级(1-5)
     */
    private Integer severityLevel;

    /**
     * 0-处理中 1-成功 2-失败
     */
    private Integer status;

    /**
     * 报告人ID
     */
    private Long reporterId;

    /**
     * 处理意见
     */
    private String processComment;

    /**
     * 处理人ID
     */
    private Long processBy;

    /**
     * 处理时间
     */
    private Date processTime;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
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