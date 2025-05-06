package com.monitor.web.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.monitor.common.common.BaseResponse;
import com.monitor.common.common.ErrorCode;
import com.monitor.common.common.ResultUtils;
import com.monitor.common.exception.BusinessException;
import com.monitor.oss.utils.MinioUtil;
import com.monitor.system.model.dto.admin.LoginRequest;
import com.monitor.system.model.vo.AdminVo;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RequiredArgsConstructor
@RestController
@RequestMapping("invoke")
@Slf4j
public class ApiController {
    private final MinioUtil minioUtil;
    @PostMapping("/violation")
    public void upload(Object object) throws Exception {
        log.info("收到分析中心的结果");
    }
    @GetMapping("/get")
    public String get() throws Exception {
        return "assets/img/img1.png";
    }
}
