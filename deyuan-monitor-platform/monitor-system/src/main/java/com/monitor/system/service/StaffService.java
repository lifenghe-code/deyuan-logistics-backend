package com.monitor.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monitor.system.model.domain.Staff;
import com.baomidou.mybatisplus.extension.service.IService;
import com.monitor.system.model.dto.staff.StaffQueryRequest;

/**
* @author li_fe
* @description 针对表【staff】的数据库操作Service
* @createDate 2025-04-01 11:24:52
*/
public interface StaffService extends IService<Staff> {
    QueryWrapper<Staff> getQueryWrapper(StaffQueryRequest staffQueryRequest);


}
