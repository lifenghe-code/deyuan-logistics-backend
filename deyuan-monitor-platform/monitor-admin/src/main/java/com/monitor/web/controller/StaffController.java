package com.monitor.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monitor.common.common.BaseResponse;
import com.monitor.common.common.DeleteRequest;
import com.monitor.common.common.ErrorCode;
import com.monitor.common.common.ResultUtils;
import com.monitor.common.exception.BusinessException;
import com.monitor.common.exception.ThrowUtils;
import com.monitor.system.model.domain.Record;
import com.monitor.system.model.domain.Staff;
import com.monitor.system.model.dto.record.RecordAddRequest;
import com.monitor.system.model.dto.record.RecordUpdateRequest;
import com.monitor.system.model.dto.staff.StaffAddRequest;
import com.monitor.system.model.dto.staff.StaffQueryRequest;
import com.monitor.system.model.dto.staff.StaffUpdateRequest;
import com.monitor.system.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/staff")
public class StaffController {
    private final StaffService staffService;

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param staffQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    //@SaCheckLogin
    public BaseResponse<Page<Staff>> listStaffByPage(@RequestBody StaffQueryRequest staffQueryRequest) {
        // 获取当前会话账号id, 如果未登录，则抛出异常：`NotLoginException`
        long current = staffQueryRequest.getCurrent();
        long size = staffQueryRequest.getPageSize();

        Page<Staff> staffPage = staffService.page(new Page<>(current, size),
                staffService.getQueryWrapper(staffQueryRequest));
        return ResultUtils.success(staffPage);
    }


    @PostMapping("/add")
    //@SaCheckLogin
    public BaseResponse<Long> addStaff(@RequestBody StaffAddRequest staffAddRequest) {
        if (staffAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Staff staff = new Staff();
        BeanUtils.copyProperties(staffAddRequest, staff);
        boolean result = staffService.save(staff);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long staffId = staff.getStaffId();
        return ResultUtils.success(staffId);
    }

    @PostMapping("/delete")
    @SaCheckLogin
    public BaseResponse<Boolean> deleteStaff(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        Staff oldStaff = staffService.getById(id);
        ThrowUtils.throwIf(oldStaff == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除

        boolean b = staffService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param staffUpdateRequest
     * @return
     */
    @PostMapping("/update")
    // @SaCheckLogin
    public BaseResponse<Boolean> updateStaff(@RequestBody StaffUpdateRequest staffUpdateRequest) {
        if (staffUpdateRequest == null || staffUpdateRequest.getStaffId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Staff staff = new Staff();
        BeanUtils.copyProperties(staffUpdateRequest, staff);

        long staffId = staffUpdateRequest.getStaffId();
        // 判断是否存在
        Staff oldStaff= staffService.getById(staffId);
        ThrowUtils.throwIf(oldStaff == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = staffService.updateById(staff);
        return ResultUtils.success(result);
    }
}
