package com.monitor.system.model.dto.notice;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class NoticeUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;


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
    private Integer status;
}
