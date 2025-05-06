package com.monitor.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.monitor.common.common.BaseResponse;
import com.monitor.common.common.ErrorCode;
import com.monitor.common.common.ResultUtils;
import com.monitor.common.exception.BusinessException;
import com.monitor.system.model.dto.admin.LoginRequest;
import com.monitor.system.model.vo.AdminVo;
import com.monitor.system.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    private final AdminService adminService;
    /**
     * 登录方法
     *
     * @param loginRequest 登录信息
     * @return 结果
     */
    @SaIgnore
    @PostMapping("/login")
    public BaseResponse<AdminVo> login(@RequestBody LoginRequest loginRequest) {

        if (loginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String account = loginRequest.getAccount();
        String password = loginRequest.getPassword();
        if (StringUtils.isAnyBlank(account, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AdminVo adminVo = adminService.adminLogin(account, password);
        long loginId = StpUtil.getLoginIdAsLong();// 获取当前会话账号id, 并转化为`long`类型
        log.info("登录用户的ID"+ loginId);
        return ResultUtils.success(adminVo);
    }


}
