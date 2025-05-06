package com.monitor.system.service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.monitor.system.model.domain.Appeal;
import com.monitor.system.model.dto.appeal.AppealQueryRequest;


/**
* @author li_fe
* @description 针对表【appeal】的数据库操作Service
* @createDate 2025-04-18 15:33:21
*/
public interface AppealService extends IService<Appeal> {
    QueryWrapper<Appeal> getQueryWrapper(AppealQueryRequest appealQueryRequest);

    Page<Appeal> searchAppeal(AppealQueryRequest appealQueryRequest);
}
