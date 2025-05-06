package com.monitor.system.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.monitor.system.model.domain.Notice;
import com.monitor.system.model.domain.Record;
import com.monitor.system.model.dto.notice.NoticeQueryRequest;
import com.monitor.system.model.dto.record.RecordQueryRequest;

/**
* @author li_fe
* @description 针对表【notice】的数据库操作Service
* @createDate 2025-04-11 14:06:30
*/
public interface NoticeService extends IService<Notice> {
    Page<Notice> searchRecord(NoticeQueryRequest noticeQueryRequest);
    QueryWrapper<Notice> getQueryWrapper(NoticeQueryRequest noticeQueryRequest);


}
