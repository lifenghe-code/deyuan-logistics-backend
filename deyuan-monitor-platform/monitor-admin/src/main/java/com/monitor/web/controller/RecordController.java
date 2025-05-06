package com.monitor.web.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.ObjectMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monitor.common.common.BaseResponse;
import com.monitor.common.common.DeleteRequest;
import com.monitor.common.common.ErrorCode;
import com.monitor.common.common.ResultUtils;
import com.monitor.common.exception.BusinessException;
import com.monitor.common.exception.ThrowUtils;
import com.monitor.oss.utils.MinioUtil;
import com.monitor.system.model.domain.Record;
import com.monitor.system.model.dto.record.RecordAddRequest;
import com.monitor.system.model.dto.record.RecordQueryRequest;
import com.monitor.system.model.dto.record.RecordUpdateRequest;
import com.monitor.system.service.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@RestController
@RequestMapping("/record")
@Slf4j
public class RecordController {
    private final RecordService recordService;

    @Value("${minio.bucket-name}")
    String bucketName;

    private final MinioUtil minioUtil;

    @PostMapping("/list/page")
    // @SaCheckLogin
    public BaseResponse<Page<Record>> listRecordByPage(@RequestBody RecordQueryRequest recordQueryRequest) {
        // long loginId = StpUtil.getLoginIdAsLong();// 获取当前会话账号id, 并转化为`long`类型
        Page<Record> recordPage =
                recordService.searchRecord(recordQueryRequest);
        return ResultUtils.success(recordPage);
    }

    /**
     *
     * @param recordAddRequest
     * @return
     */
    @PostMapping("/add")
    // @SaCheckLogin
    public BaseResponse<Long> addRecord(@RequestPart("file") MultipartFile multipartFile, RecordAddRequest recordAddRequest) throws Exception {

        if (recordAddRequest == null || multipartFile == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String filePath = minioUtil.uploadFile(multipartFile, bucketName);
        // long loginId = StpUtil.getLoginIdAsLong();// 获取当前会话账号id, 并转化为`long`类型
        Record record = new Record();
        BeanUtils.copyProperties(recordAddRequest, record);
        // record.setProcessBy(loginId);
        record.setEvidence(filePath);
        boolean result = recordService.save(record);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long recordId = record.getRecordId();
        return ResultUtils.success(recordId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    // @SaCheckLogin
    public BaseResponse<Boolean> deleteRecord(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long loginId = StpUtil.getLoginIdAsLong();// 获取当前会话账号id, 并转化为`long`类型
        long id = deleteRequest.getId();
        // 判断是否存在
        Record oldRecord = recordService.getById(id);
        ThrowUtils.throwIf(oldRecord == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除

        boolean b = recordService.removeById(id);
        return ResultUtils.success(b);

    }

    /**
     * 更新（仅管理员）
     *
     * @param recordUpdateRequest
     * @return
     */
    @PostMapping("/update")
    // @SaCheckLogin
    public BaseResponse<Boolean> updateRecord(@RequestBody RecordUpdateRequest recordUpdateRequest) {
        if (recordUpdateRequest == null || recordUpdateRequest.getRecordId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Record record = new Record();
        BeanUtils.copyProperties(recordUpdateRequest, record);

        long recordId = recordUpdateRequest.getRecordId();
        // 判断是否存在
        Record oldRecord = recordService.getById(recordId);
        ThrowUtils.throwIf(oldRecord == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = recordService.updateById(record);
        return ResultUtils.success(result);
    }
}
