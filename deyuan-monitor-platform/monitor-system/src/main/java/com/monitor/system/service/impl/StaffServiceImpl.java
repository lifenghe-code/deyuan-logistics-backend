package com.monitor.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.monitor.common.common.ErrorCode;
import com.monitor.common.constant.CommonConstant;
import com.monitor.common.exception.BusinessException;
import com.monitor.common.utils.SqlUtils;
import com.monitor.system.model.domain.Staff;
import com.monitor.system.model.dto.staff.StaffQueryRequest;
import com.monitor.system.service.StaffService;
import com.monitor.system.mapper.StaffMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author li_fe
* @description 针对表【staff】的数据库操作Service实现
* @createDate 2025-04-01 11:24:52
*/
@Service
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff>
    implements StaffService{
    @Override
    public QueryWrapper<Staff> getQueryWrapper(StaffQueryRequest staffQueryRequest) {
        if (staffQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long staffId = staffQueryRequest.getStaffId();
        String staffName = staffQueryRequest.getStaffName();
        String plateNumber = staffQueryRequest.getPlateNumber();
        String email = staffQueryRequest.getEmail();
        String phone = staffQueryRequest.getPhone();
        Integer staffType = staffQueryRequest.getStaffType();
        Long deptId = staffQueryRequest.getDeptId();
        Integer grade = staffQueryRequest.getGrade();
        QueryWrapper<Staff> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(staffId != null, "staff_id", staffId);
        queryWrapper.like(StringUtils.isNotBlank(staffName), "staff_name", staffName);
        queryWrapper.like(StringUtils.isNotBlank(plateNumber), "plate_number", plateNumber);
        queryWrapper.eq(StringUtils.isNotBlank(email), "email", email);
        queryWrapper.eq(StringUtils.isNotBlank(phone), "phone", phone);
        queryWrapper.eq(staffType != null, "staff_type", staffType);
        queryWrapper.eq(deptId != null, "dept_Id", deptId);
        queryWrapper.eq(grade != null, "grade", grade);
        String sortField = staffQueryRequest.getSortField();
        String sortOrder = staffQueryRequest.getSortOrder();
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}




