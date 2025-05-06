package com.monitor.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monitor.common.common.BaseResponse;
import com.monitor.common.common.ErrorCode;
import com.monitor.common.common.ResultUtils;
import com.monitor.common.exception.BusinessException;
import com.monitor.common.exception.ThrowUtils;
import com.monitor.system.model.domain.Dept;
import com.monitor.system.model.dto.dept.DeptQueryRequest;
import com.monitor.system.model.dto.dept.DeptUpdateRequest;
import com.monitor.system.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dept")
public class DeptController {
    private final DeptService deptService;

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param deptQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    //@SaCheckLogin
    public BaseResponse<Page<Dept>> listStaffByPage(@RequestBody DeptQueryRequest deptQueryRequest) {
        // 获取当前会话账号id, 如果未登录，则抛出异常：`NotLoginException`
        long current = deptQueryRequest.getCurrent();
        long size = deptQueryRequest.getPageSize();

        Page<Dept> staffPage = deptService.page(new Page<>(current, size),
                deptService.getQueryWrapper(deptQueryRequest));
        return ResultUtils.success(staffPage);
    }
    /**
     * 更新（仅管理员）
     *
     * @param deptUpdateRequest
     * @return
     */
    @PostMapping("/update")
    // @SaCheckLogin
    public BaseResponse<Boolean> updateStaff(@RequestBody DeptUpdateRequest deptUpdateRequest) {
        if (deptUpdateRequest == null || deptUpdateRequest.getDeptId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Dept dept = new Dept();
        BeanUtils.copyProperties(deptUpdateRequest, dept);

        long deptId = deptUpdateRequest.getDeptId();
        // 判断是否存在
        Dept oldStaff= deptService.getById(deptId);
        ThrowUtils.throwIf(oldStaff == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = deptService.updateById(dept);
        return ResultUtils.success(result);
    }
}
