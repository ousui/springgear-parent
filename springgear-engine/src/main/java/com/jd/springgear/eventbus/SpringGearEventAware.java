package com.jd.springgear.eventbus;

import org.springframework.beans.factory.Aware;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2020/12/28
 **/
public interface SpringGearEventAware extends Aware {

    void set(Class<SpringGearEventListener> listeners);
}
