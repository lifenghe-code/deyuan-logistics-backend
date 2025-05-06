package com.monitor.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.monitor.common.constant.CommonConstant;
import com.monitor.common.utils.SqlUtils;
import com.monitor.system.mapper.NoticeMapper;
import com.monitor.system.model.domain.Notice;
import com.monitor.system.model.dto.notice.NoticeQueryRequest;
import com.monitor.system.service.NoticeService;
import org.springframework.stereotype.Service;

/**
* @author li_fe
* @description 针对表【notice】的数据库操作Service实现
* @createDate 2025-04-11 14:06:30
*/
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice>
    implements NoticeService {
    @Override
    public Page<Notice> searchRecord(NoticeQueryRequest noticeQueryRequest) {
        QueryWrapper<Notice> queryWrapper = this.getQueryWrapper(noticeQueryRequest);
        long current = noticeQueryRequest.getCurrent() - 1;
        long pageSize = noticeQueryRequest.getPageSize();
        Page<Notice> page = new Page<>(current, pageSize);
        Page<Notice> recordList = this.page(page,queryWrapper);
        return recordList;
    }
    public QueryWrapper<Notice> getQueryWrapper(NoticeQueryRequest noticeQueryRequest){
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        if (noticeQueryRequest == null) {
            return queryWrapper;
        }
        String staffId = noticeQueryRequest.getStaffId();
        Long copilotId = noticeQueryRequest.getCopilotId();
        String plateNumber = noticeQueryRequest.getPlateNumber();
        Integer behaviorCode = noticeQueryRequest.getBehaviorCode();
        queryWrapper.eq(ObjectUtils.isNotEmpty(staffId), "staff_id", staffId)
                .eq(ObjectUtils.isNotEmpty(copilotId), "copilot_id", copilotId)
                .eq(ObjectUtils.isNotEmpty(plateNumber), "plate_number", plateNumber)
                .eq(ObjectUtils.isNotEmpty(behaviorCode), "behavior_code", behaviorCode);
        String sortField = noticeQueryRequest.getSortField();
        String sortOrder = noticeQueryRequest.getSortOrder();
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

}




