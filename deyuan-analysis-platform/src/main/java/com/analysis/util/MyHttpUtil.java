package com.analysis.util;

import cn.hutool.http.HttpUtil;
import com.analysis.enums.BehaviorEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
@Slf4j
public class MyHttpUtil {

    public String postHttpUtil(String url, String clientId, BehaviorEnum behaviorEnum) {
        Map<String, Object> params = new HashMap<>();
        params.put("客户端ID", clientId);
        params.put("行为编号", behaviorEnum.getBehaviorCode());
        params.put("行为描述", behaviorEnum.getBehaviorName());
        String postResult = HttpUtil.post(url, params);
        log.info("请求结果为:{}", postResult);
        return postResult;
    }

}
