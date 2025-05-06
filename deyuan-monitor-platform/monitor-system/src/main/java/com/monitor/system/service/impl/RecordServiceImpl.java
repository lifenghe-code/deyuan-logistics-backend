package com.monitor.system.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.monitor.common.constant.CommonConstant;
import com.monitor.common.utils.SqlUtils;
import com.monitor.system.mapper.RecordMapper;
import com.monitor.system.model.domain.Record;
import com.monitor.system.model.dto.record.RecordQueryRequest;
import com.monitor.system.service.RecordService;
import org.springframework.stereotype.Service;

/**
* @author li_fe
* @description 针对表【illegal_record】的数据库操作Service实现
* @createDate 2025-04-09 17:04:32
*/
@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record>
    implements RecordService {
    @Override
    public Page<Record> searchRecord(RecordQueryRequest recordQueryRequest) {
        QueryWrapper<Record> queryWrapper = this.getQueryWrapper(recordQueryRequest);
        long current = recordQueryRequest.getCurrent() - 1;
        long pageSize = recordQueryRequest.getPageSize();
        Page<Record> page = new Page<>(current, pageSize);
        Page<Record> recordList = this.page(page,queryWrapper);
        return recordList;
    }

    @Override
    public QueryWrapper<Record> getQueryWrapper(RecordQueryRequest recordQueryRequest) {
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        if (recordQueryRequest == null) {
            return queryWrapper;
        }

        Long driverId = recordQueryRequest.getDriverId();
        Long copilotId = recordQueryRequest.getCopilotId();
        String driverName = recordQueryRequest.getDriverName();
        String copilotName = recordQueryRequest.getCopilotName();
        String plateNumber = recordQueryRequest.getPlateNumber();
        Long behaviorId = recordQueryRequest.getBehaviorId();
        Integer behaviorCode = recordQueryRequest.getBehaviorCode();
        Date occurrenceTime = recordQueryRequest.getOccurrenceTime();
        Date discoveryTime = recordQueryRequest.getDiscoveryTime();
        Integer discoveryMethod = recordQueryRequest.getDiscoveryMethod();
        String description = recordQueryRequest.getDescription();
        Integer severityLevel = recordQueryRequest.getSeverityLevel();
        Integer status = recordQueryRequest.getStatus();
        Long reporterId = recordQueryRequest.getReporterId();
        String processComment = recordQueryRequest.getProcessComment();
        Long processBy = recordQueryRequest.getProcessBy();
        Date processTime = recordQueryRequest.getProcessTime();
        Date queryStartTime = recordQueryRequest.getQueryStartTime();
        Date queryEndTime = recordQueryRequest.getQueryEndTime();
        queryWrapper.eq(ObjectUtils.isNotEmpty(driverId), "driver_id", driverId)
                .eq(ObjectUtils.isNotEmpty(copilotId), "copilot_id", copilotId)
                .like(ObjectUtils.isNotEmpty(driverName), "driver_name", driverName) // 模糊查询
                .like(ObjectUtils.isNotEmpty(copilotName), "copilot_name", copilotName) // 模糊查询
                .eq(ObjectUtils.isNotEmpty(plateNumber), "plate_number", plateNumber)
                .eq(ObjectUtils.isNotEmpty(behaviorId), "behavior_id", behaviorId)
                .eq(ObjectUtils.isNotEmpty(behaviorCode), "behavior_code", behaviorCode)
                .eq(ObjectUtils.isNotEmpty(discoveryMethod), "discovery_method", discoveryMethod)
                .like(ObjectUtils.isNotEmpty(description), "description", description) // 模糊查询
                .eq(ObjectUtils.isNotEmpty(severityLevel), "severity_level", severityLevel)
                .eq(ObjectUtils.isNotEmpty(status), "status", status)
                .eq(ObjectUtils.isNotEmpty(reporterId), "reporter_id", reporterId)
                .like(ObjectUtils.isNotEmpty(processComment), "process_comment", processComment)
                .eq(ObjectUtils.isNotEmpty(processBy), "process_by", processBy)
                .ge(ObjectUtils.isNotEmpty(queryStartTime), "occurrence_time", queryStartTime)
                .lt(ObjectUtils.isNotEmpty(queryEndTime), "occurrence_time", queryEndTime);
        String sortField = recordQueryRequest.getSortField();
        String sortOrder = recordQueryRequest.getSortOrder();
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}




