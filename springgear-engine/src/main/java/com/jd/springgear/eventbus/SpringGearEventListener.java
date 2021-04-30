package com.jd.springgear.eventbus;

import com.jd.springgear.engine.context.SpringGearContext;

/**
 * 基础 spring gear event 注解的监听实现
 *
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @see com.jd.springgear.eventbus.annotation.SpringGearEvent
 * @since 2020/12/26
 **/
public interface SpringGearEventListener<REQ, RESP> {

    void process(SpringGearContext<REQ, RESP> context);

    void ex(SpringGearContext<REQ, RESP> context, Throwable e);
}
