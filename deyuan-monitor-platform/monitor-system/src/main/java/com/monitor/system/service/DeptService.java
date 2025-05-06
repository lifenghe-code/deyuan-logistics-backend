package com.monitor.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monitor.system.model.domain.Dept;
import com.baomidou.mybatisplus.extension.service.IService;
import com.monitor.system.model.domain.Staff;
import com.monitor.system.model.dto.dept.DeptQueryRequest;
import com.monitor.system.model.dto.staff.StaffQueryRequest;

/**
* @author li_fe
* @description 针对表【dept】的数据库操作Service
* @createDate 2025-04-01 11:18:55
*/
public interface DeptService extends IService<Dept> {
    QueryWrapper<Dept> getQueryWrapper(DeptQueryRequest deptQueryRequest);

}
