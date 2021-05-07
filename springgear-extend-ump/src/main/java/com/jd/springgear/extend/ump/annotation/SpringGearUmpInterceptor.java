package com.jd.springgear.extend.ump.annotation;

import com.jd.springgear.beans.interceptor.SpringGearInterceptor;
import com.jd.springgear.exception.SpringGearException;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.springframework.core.annotation.Order;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2021/05/07
 **/
@Order
public class SpringGearUmpInterceptor implements SpringGearInterceptor<CallerInfo> {

    @Override
    public CallerInfo beforeExecute(String beanName, Object request, long timestamp) throws Exception {
        return Profiler.registerInfo(beanName, true, true);
    }

    @Override
    public void afterExecute(String beanName, Object request, long timestamp, CallerInfo preResult, Object response) throws Exception {

    }

    @Override
    public void onFinally(String beanName, Object request, long timestamp, CallerInfo preResult, Object response, Exception ex) {
        Profiler.registerInfoEnd(preResult);
    }

    @Override
    public void onException(String beanName, Object request, long timestamp, CallerInfo preResult, Object response, SpringGearException ex) {
        Profiler.functionError(preResult);
    }
}
