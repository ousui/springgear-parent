package com.jd.springgear.eventbus;

import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.Aware;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2020/12/28
 **/
public interface EventBusAware extends Aware {

    void setEventBus(EventBus eventBus);
}
