package com.jd.springgear.engine.context;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 通用 map wrapper
 *
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2021/03/25
 **/
public class SpringGearResultMapWrapper<R> implements SpringGearResultWrapper<Map<String, Object>> {
    @Override
    public Map<String, Object> process(Object req, Object resp, long timestamp, int code, String msg, Object... others) {
        Map<String, Object> result = Maps.newHashMap();

        result.put("ts", timestamp);
        result.put("msg", msg);
        result.put("code", code);
        result.put("data", resp);

        return result;
    }
}
