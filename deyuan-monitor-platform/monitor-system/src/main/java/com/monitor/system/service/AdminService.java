package com.monitor.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.monitor.system.model.domain.Admin;
import com.monitor.system.model.vo.AdminVo;

import javax.servlet.http.HttpServletRequest;


/**
* @author li_fe
* @description 针对表【admin】的数据库操作Service
* @createDate 2025-04-01 11:13:01
*/
public interface AdminService extends IService<Admin> {

    AdminVo adminLogin(String account, String password);
    AdminVo getLoginVo(Admin admin);
}
