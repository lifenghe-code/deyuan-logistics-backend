package com.monitor.system.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName appeal
 */
@TableName(value ="appeal")
@Data
public class Appeal implements Serializable {
    /**
     * Id
     */
    @TableId(type = IdType.AUTO)
    private Long appealId;

    /**
     * 关联的违规记录ID
     */
    private Long recordId;

    /**
     * 申诉员工ID
     */
    private Long staffId;

    /**
     * 申诉人员姓名
     */
    private String staffName;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 申诉类型
     */
    private Integer appealType;

    /**
     * 申诉理由
     */
    private String appealReason;

    /**
     * 申诉时间
     */
    private Date appealTime;

    /**
     * 0-申诉中 1-申诉成功 2-申诉拒绝 3-申诉撤回
     */
    private Integer status;

    /**
     * 申诉证据(图片/文件/视频等)
     */
    private String evidence;

    /**
     * 处理人ID
     */
    private String processorId;

    /**
     * 处理意见
     */
    private String processComment;

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