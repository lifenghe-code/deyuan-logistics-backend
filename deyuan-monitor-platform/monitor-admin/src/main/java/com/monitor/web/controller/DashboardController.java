package com.monitor.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monitor.common.common.BaseResponse;
import com.monitor.common.common.ResultUtils;
import com.monitor.system.mapper.RecordMapper;
import com.monitor.system.model.domain.Record;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {
    @Resource
    private RecordMapper recordMapper;
    // 各类违规情况的统计
    @GetMapping("/statistics")
    //@SaCheckLogin
    public BaseResponse<Map<String, Map<String, Long>>> countViolationsByTypeAndDay() {
        // 获取本周的开始(周一)和结束(周日)时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);

        // 查询本周所有违规记录
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("occurrence_time", startOfWeek, endOfWeek);
        List<Record> records = recordMapper.selectList(queryWrapper);

        // 先按违规类型分组，再按星期几分组统计
        Map<String, Map<String, Long>> result = records.stream()
                .collect(Collectors.groupingBy(
                        record -> String.valueOf(record.getBehaviorCode()),
                        Collectors.groupingBy(
                                record -> record.getOccurrenceTime().toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                        .getDayOfWeek()
                                        .getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                                LinkedHashMap::new,  // 使用LinkedHashMap保持顺序
                                Collectors.counting()
                        )
                ));

        // 补全7天并保证顺序
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        result.forEach((behaviorCode, dayMap) -> {
            LinkedHashMap<String, Long> orderedMap = new LinkedHashMap<>();
            for (String day : daysOfWeek) {
                orderedMap.put(day, dayMap.getOrDefault(day, 0L));
            }
            result.put(behaviorCode, orderedMap); // 替换原来的无序Map
        });

        return ResultUtils.success(result);
    }


    @GetMapping("/number-violations")
    public BaseResponse<Map<String, Long>> countViolationsByType() {
        // 获取本周的开始(周一)和结束(周日)时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);

        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("occurrence_time", startOfWeek, endOfWeek);
        queryWrapper
                .select("behavior_code, COUNT(*) as count")
                .groupBy("behavior_code");

        List<Map<String, Object>> maps = recordMapper.selectMaps(queryWrapper);

        Map<String, Long> collect = maps.stream()
                .collect(Collectors.toMap(
                        map -> (String) map.get("behavior_code"),
                        map -> (Long) map.get("count")
                ));
        return ResultUtils.success(collect);
    }
    @GetMapping("/list")
    // @SaCheckLogin
    public BaseResponse<List<Record>> listRecordByPage() {
        // long loginId = StpUtil.getLoginIdAsLong();// 获取当前会话账号id, 并转化为`long`类型
        LambdaQueryWrapper<Record> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(Record::getOccurrenceTime)  // 使用Lambda表达式指定排序字段
                .last("LIMIT 5");

        List<Record> records = recordMapper.selectList(queryWrapper);
        return ResultUtils.success(records);
    }
}
