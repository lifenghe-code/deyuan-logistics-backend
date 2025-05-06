package com.monitor.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.monitor.system.mapper.BehaviorMapper;
import com.monitor.system.model.domain.Behavior;
import com.monitor.system.service.BehaviorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BehaviorServiceImpl extends ServiceImpl<BehaviorMapper, Behavior>
        implements BehaviorService {
    @Override
    public List<Behavior> listIllegalBehavior() {
        return this.list();
    }
}
