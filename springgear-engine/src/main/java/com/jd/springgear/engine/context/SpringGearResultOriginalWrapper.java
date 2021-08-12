package com.jd.springgear.engine.context;

import lombok.extern.slf4j.Slf4j;

/**
 * 原样返回
 *
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2021/03/25
 **/
@Slf4j
public class SpringGearResultOriginalWrapper<R> implements SpringGearResultWrapper<Object> {
    @Override
    public Object process(Object req, Object resp, long timestamp, int code, String msg, Object... others) {
        return resp;
    }
}
