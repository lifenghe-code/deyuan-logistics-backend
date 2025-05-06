package com.monitor.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.monitor.system.model.domain.Behavior;

import java.util.List;

public interface BehaviorService extends IService<Behavior> {
    /**
     * @return
     */
    List<Behavior> listIllegalBehavior();
}
