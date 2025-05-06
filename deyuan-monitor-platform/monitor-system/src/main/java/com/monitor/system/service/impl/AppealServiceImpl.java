package com.monitor.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.monitor.common.constant.CommonConstant;
import com.monitor.common.utils.SqlUtils;
import com.monitor.system.mapper.AppealMapper;
import com.monitor.system.model.domain.Appeal;
import com.monitor.system.model.dto.appeal.AppealQueryRequest;
import com.monitor.system.service.AppealService;
import org.springframework.stereotype.Service;

/**
* @author li_fe
* @description 针对表【appeal】的数据库操作Service实现
* @createDate 2025-04-18 15:33:21
*/
@Service
public class AppealServiceImpl extends ServiceImpl<AppealMapper, Appeal>
    implements AppealService {
    @Override
    public QueryWrapper<Appeal> getQueryWrapper(AppealQueryRequest appealQueryRequest){
        QueryWrapper<Appeal> queryWrapper = new QueryWrapper<>();
        if (appealQueryRequest == null) {
            return queryWrapper;
        }
        Long recordId = appealQueryRequest.getRecordId();
        Long staffId = appealQueryRequest.getStaffId();
        String staffName = appealQueryRequest.getStaffName();
        String plateNumber = appealQueryRequest.getPlateNumber();
        Integer appealType = appealQueryRequest.getAppealType();


        queryWrapper.eq(ObjectUtils.isNotEmpty(recordId), "record_id", recordId)
                .eq(ObjectUtils.isNotEmpty(staffId), "staff_id", staffId)
                .like(ObjectUtils.isNotEmpty(staffName), "staff_name", staffName) // 模糊查询
                .eq(ObjectUtils.isNotEmpty(appealType), "appeal_type", appealType)
                .like(ObjectUtils.isNotEmpty(plateNumber), "plate_number", plateNumber); // 模糊查询

        String sortField = appealQueryRequest.getSortField();
        String sortOrder = appealQueryRequest.getSortOrder();
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;


    }
    @Override
    public Page<Appeal> searchAppeal(AppealQueryRequest appealQueryRequest){
        QueryWrapper<Appeal> queryWrapper = this.getQueryWrapper(appealQueryRequest);
        long current = appealQueryRequest.getCurrent() - 1;
        long pageSize = appealQueryRequest.getPageSize();
        Page<Appeal> page = new Page<>(current, pageSize);
        Page<Appeal> appealList = this.page(page,queryWrapper);
        return appealList;
    }

}




