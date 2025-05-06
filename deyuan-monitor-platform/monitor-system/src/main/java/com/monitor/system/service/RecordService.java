package com.monitor.system.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.monitor.system.model.domain.Record;
import com.monitor.system.model.dto.record.RecordQueryRequest;

/**
* @author li_fe
* @description 针对表【illegal_record】的数据库操作Service
* @createDate 2025-04-09 17:04:32
*/
public interface RecordService extends IService<Record> {
    QueryWrapper<Record> getQueryWrapper(RecordQueryRequest recordQueryRequest);

    Page<Record> searchRecord(RecordQueryRequest recordQueryRequest);
}
