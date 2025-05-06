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
import com.monitor.system.model.domain.Notice;
import com.monitor.system.model.dto.notice.NoticeAddRequest;
import com.monitor.system.model.dto.notice.NoticeQueryRequest;
import com.monitor.system.model.dto.notice.NoticeUpdateRequest;
import com.monitor.system.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notice")
@Slf4j
public class NoticeController {
    private final NoticeService noticeService;


    @PostMapping("/list/page")
    @SaCheckLogin
    public BaseResponse<Page<Notice>> listNoticeByPage(@RequestBody NoticeQueryRequest noticeQueryRequest) {
        Page<Notice> recordPage =
                noticeService.searchRecord(noticeQueryRequest);
        return ResultUtils.success(recordPage);
    }


    /**
     *
     * @param noticeAddRequest
     * @return
     */
    @PostMapping("/add")
    @SaCheckLogin
    public BaseResponse<Long> addNotice(@RequestBody NoticeAddRequest noticeAddRequest) {
        if (noticeAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // long loginId = StpUtil.getLoginIdAsLong();// 获取当前会话账号id, 并转化为`long`类型
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeAddRequest, notice);
        boolean result = noticeService.save(notice);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long recordId = notice.getNoticeId();
        return ResultUtils.success(recordId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @SaCheckLogin
    public BaseResponse<Boolean> deleteNotice(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long loginId = StpUtil.getLoginIdAsLong();// 获取当前会话账号id, 并转化为`long`类型
        long id = deleteRequest.getId();
        // 判断是否存在
        Notice oldNotice = noticeService.getById(id);
        ThrowUtils.throwIf(oldNotice == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除

        boolean b = noticeService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param noticeUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @SaCheckLogin
    public BaseResponse<Boolean> updateNotice(@RequestBody NoticeUpdateRequest noticeUpdateRequest) {
        if (noticeUpdateRequest == null || noticeUpdateRequest.getNoticeId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeUpdateRequest, notice);

        long noticeId = noticeUpdateRequest.getNoticeId();
        // 判断是否存在
        Notice oldNotice = noticeService.getById(noticeId);
        ThrowUtils.throwIf(oldNotice == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = noticeService.updateById(notice);
        return ResultUtils.success(result);
    }
}
