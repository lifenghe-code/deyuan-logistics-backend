package com.monitor.system.model.dto.record;

import com.monitor.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class RecordQueryRequest extends PageRequest implements Serializable {
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
     * 查询开始时间
     */
    private Date queryStartTime;

    /**
     * 查询结束时间
     */
    private Date queryEndTime;


    private static final long serialVersionUID = 1L;
}
