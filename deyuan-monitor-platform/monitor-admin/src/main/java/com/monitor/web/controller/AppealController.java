package com.monitor.web.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monitor.common.common.BaseResponse;
import com.monitor.common.common.DeleteRequest;
import com.monitor.common.common.ErrorCode;
import com.monitor.common.common.ResultUtils;
import com.monitor.common.exception.BusinessException;
import com.monitor.common.exception.ThrowUtils;
import com.monitor.system.model.domain.Appeal;
import com.monitor.system.model.domain.Dept;
import com.monitor.system.model.domain.Record;
import com.monitor.system.model.dto.appeal.AppealAddRequest;
import com.monitor.system.model.dto.appeal.AppealQueryRequest;
import com.monitor.system.model.dto.appeal.AppealUpdateRequest;
import com.monitor.system.model.dto.dept.DeptQueryRequest;
import com.monitor.system.model.dto.record.RecordAddRequest;
import com.monitor.system.model.dto.record.RecordUpdateRequest;
import com.monitor.system.service.AppealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/appeal")
@Slf4j
public class AppealController {

    private final AppealService appealService;

    @PostMapping("/list/page")
    //@SaCheckLogin
    public BaseResponse<Page<Appeal>> listAppealByPage(@RequestBody AppealQueryRequest appealQueryRequest) {
        // 获取当前会话账号id, 如果未登录，则抛出异常：`NotLoginException`
        Page<Appeal> appealPage =
                appealService.searchAppeal(appealQueryRequest);
        return ResultUtils.success(appealPage);
    }

    @PostMapping("/add")
    // @SaCheckLogin
    public BaseResponse<Long> addAppeal(@RequestBody AppealAddRequest appealAddRequest) throws Exception {

        if (appealAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Appeal appeal = new Appeal();
        BeanUtils.copyProperties(appealAddRequest, appeal);
        // record.setProcessBy(loginId);

        boolean result = appealService.save(appeal);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long appealId = appeal.getRecordId();
        return ResultUtils.success(appealId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    // @SaCheckLogin
    public BaseResponse<Boolean> deleteAppeal(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long loginId = StpUtil.getLoginIdAsLong();// 获取当前会话账号id, 并转化为`long`类型
        long id = deleteRequest.getId();
        // 判断是否存在
        Appeal oldAppeal = appealService.getById(id);
        ThrowUtils.throwIf(oldAppeal == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除

        boolean b = appealService.removeById(id);
        return ResultUtils.success(b);

    }

    /**
     * 更新（仅管理员）
     *
     * @param appealUpdateRequest
     * @return
     */
    @PostMapping("/update")
    // @SaCheckLogin
    public BaseResponse<Boolean> updateAppeal(@RequestBody AppealUpdateRequest appealUpdateRequest) {
        if (appealUpdateRequest == null || appealUpdateRequest.getRecordId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Appeal appeal = new Appeal();
        BeanUtils.copyProperties(appealUpdateRequest, appeal);

        long appealId = appealUpdateRequest.getAppealId();
        // 判断是否存在
        Appeal oldAppeal = appealService.getById(appealId);
        ThrowUtils.throwIf(oldAppeal == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = appealService.updateById(appeal);
        return ResultUtils.success(result);
    }


}
