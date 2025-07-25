package com.monitor.system.model.dto.appeal;

import com.monitor.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class AppealQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;
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


}
