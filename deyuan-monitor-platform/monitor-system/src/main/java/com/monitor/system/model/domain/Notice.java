package com.monitor.system.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName notice
 */
@TableName(value ="notice")
@Data
public class Notice implements Serializable {
    /**
     * 通知唯一标识
     */
    @TableId
    private Long noticeId;

    /**
     * 员工ID
     */
    private String staffId;

    /**
     * 押运员ID
     */
    private Long copilotId;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 行为编码
     */
    private Integer behaviorCode;

    /**
     * 关联的违规记录ID
     */
    private Long recordId;

    /**
     * 通知类型0-短信 1-电话
     */
    private Integer noticeType;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 发送人/部门ID
     */
    private Long senderId;

    /**
     * 发送方类型
     */
    private Object senderType;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 0-处理中 1-通知成功 2-通知失败
     */
    private Integer status = 0;

    /**
     * 删除标志（0代表存在， 1代表删除）
     */
    private Integer delFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}