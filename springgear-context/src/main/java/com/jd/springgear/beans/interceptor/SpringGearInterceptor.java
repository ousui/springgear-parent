package com.jd.springgear.beans.interceptor;

import com.jd.springgear.exception.SpringGearException;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2021/05/06
 **/
public interface SpringGearInterceptor<T> {

    T beforeExecute(String beanName, Object request, long timestamp) throws Exception;

    void afterExecute(String beanName, Object request, long timestamp, T preResult, Object response) throws Exception;

    void onFinally(String beanName, Object request, long timestamp, T preResult, Object response, Exception ex);

    void onException(String beanName, Object request, long timestamp, T preResult, Object response, SpringGearException ex);
}
