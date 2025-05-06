package com.monitor.system.model.dto.notice;

import com.monitor.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class NoticeQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

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
}
