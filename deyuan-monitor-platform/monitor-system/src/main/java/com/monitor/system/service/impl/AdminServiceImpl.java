package com.monitor.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.monitor.common.common.ErrorCode;
import com.monitor.common.exception.BusinessException;
import com.monitor.system.model.domain.Admin;
import com.monitor.system.mapper.AdminMapper;
import com.monitor.system.model.vo.AdminVo;
import com.monitor.system.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import static com.monitor.common.constant.LoginConstant.USER_LOGIN_STATE;

/**
* @author li_fe
* @description 针对表【admin】的数据库操作Service实现
* @createDate 2025-04-01 11:13:01
*/
@Slf4j
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin>
    implements AdminService {
    @Override
    public AdminVo adminLogin(String account, String password) {
        // 1. 校验
        if (StringUtils.isAnyBlank(account, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (account.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (password.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        // String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        // 查询用户是否存在
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        queryWrapper.eq("password", password);
        Admin admin = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (admin == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        StpUtil.login(admin.getAdminId());
        StpUtil.getSession().set(USER_LOGIN_STATE, admin);
        return this.getLoginVo(admin);
    }
    @Override
    public AdminVo getLoginVo(Admin admin) {
        if (admin == null) {
            return null;
        }
        AdminVo loginVO = new AdminVo();
        BeanUtils.copyProperties(admin, loginVO);
        return loginVO;
    }


}




