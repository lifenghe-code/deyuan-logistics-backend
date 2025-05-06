package com.monitor.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.monitor.common.common.ErrorCode;
import com.monitor.common.constant.CommonConstant;
import com.monitor.common.exception.BusinessException;
import com.monitor.common.utils.SqlUtils;
import com.monitor.system.model.domain.Dept;
import com.monitor.system.model.domain.Staff;
import com.monitor.system.model.dto.dept.DeptQueryRequest;
import com.monitor.system.service.DeptService;
import com.monitor.system.mapper.DeptMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author li_fe
* @description 针对表【dept】的数据库操作Service实现
* @createDate 2025-04-01 11:18:55
*/
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept>
    implements DeptService{
    public QueryWrapper<Dept> getQueryWrapper(DeptQueryRequest deptQueryRequest) {
        if (deptQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long deptId = deptQueryRequest.getDeptId();
        String deptName = deptQueryRequest.getDeptName();
        String deptDescription = deptQueryRequest.getDeptDescription();
        Long leaderId = deptQueryRequest.getLeaderId();
        String leaderName = deptQueryRequest.getLeaderName();
        Integer status = deptQueryRequest.getStatus();
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(deptId != null, "dept_id", deptId);
        queryWrapper.like(StringUtils.isNotBlank(deptName), "dept_name", deptName);
        queryWrapper.eq(leaderId!= null, "leader_id", leaderId);
        queryWrapper.like(StringUtils.isNotBlank(leaderName), "leader_name", leaderName);

        String sortField = deptQueryRequest.getSortField();
        String sortOrder = deptQueryRequest.getSortOrder();
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}




