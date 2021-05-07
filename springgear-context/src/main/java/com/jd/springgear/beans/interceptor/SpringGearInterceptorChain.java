package com.jd.springgear.beans.interceptor;

import com.google.common.collect.Maps;
import com.jd.springgear.exception.SpringGearException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2021/05/06
 **/
@Slf4j
public class SpringGearInterceptorChain implements SpringGearInterceptor<Void> {

    private final List<SpringGearInterceptor> interceptors;

    private int index = 0;

    private Map<Integer, Object> result = Maps.newHashMap();

    public SpringGearInterceptorChain(List<SpringGearInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    @Override
    public Void beforeExecute(String beanName, Object request, long timestamp) throws Exception {

        if (CollectionUtils.isEmpty(interceptors)) {
            return null;
        }

        for (int i = 0; i < this.interceptors.size(); i++) {
            Object preResult = this.interceptors.get(i).beforeExecute(beanName, request, timestamp);
            this.index = i;
            result.put(i, preResult);
        }
        return null;
    }

    @Override
    public void afterExecute(String beanName, Object request, long timestamp, Void nullVal, Object response) throws Exception {
        if (CollectionUtils.isEmpty(interceptors)) {
            return;
        }
        for (int i = this.index; i >= 0; i--) {
            Object preResult = result.get(this.index);
            this.interceptors.get(index).afterExecute(beanName, request, timestamp, preResult, response);
        }
    }

    @Override
    public void onFinally(String beanName, Object request, long timestamp, Void nullVal, Object response, Exception ex) {
        if (CollectionUtils.isEmpty(interceptors)) {
            return;
        }

        for (int i = this.index; i >= 0; i--) {
            Object preResult = result.get(this.index);
            final SpringGearInterceptor interceptor = this.interceptors.get(index);
            try {
                interceptor.onFinally(beanName, request, timestamp, preResult, response, ex);
            } catch (Exception e) {
                log.error("onFinally: there are some exception happened at interceptor {}, {}", interceptor.getClass(), e.getLocalizedMessage());
            }
        }

    }

    @Override
    public void onException(String beanName, Object request, long timestamp, Void nullVal, Object response, SpringGearException ex) {
        if (CollectionUtils.isEmpty(interceptors)) {
            return;
        }

        for (int i = this.index; i >= 0; i--) {
            Object preResult = result.get(this.index);
            final SpringGearInterceptor interceptor = this.interceptors.get(index);
            try {
                interceptor.onException(beanName, request, timestamp, preResult, response, ex);
            } catch (Exception e) {
                log.error("onException: there are some exception happened at interceptor {}, {}", interceptor.getClass(), e.getLocalizedMessage());
            }
        }

    }
}
