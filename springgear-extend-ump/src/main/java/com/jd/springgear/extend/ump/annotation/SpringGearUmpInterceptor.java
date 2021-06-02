package com.jd.springgear.extend.ump.annotation;

import com.jd.springgear.beans.interceptor.SpringGearInterceptor;
import com.jd.springgear.exception.SpringGearException;
import com.jd.springgear.support.enums.SymbolEnum;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.springframework.core.annotation.Order;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2021/05/07
 **/
@Order
public class SpringGearUmpInterceptor implements SpringGearInterceptor<CallerInfo> {

    private final static String REGEX = "[^\\w-._]";
    private final static int UMP_LENGTH = 128;


    @Override
    public CallerInfo beforeExecute(String beanName, Object request, long timestamp) throws Exception {
        // 作为 key, ump 仅支持 128 长度及 . _
        String key = this.getValidKey(beanName);

        if (key.length() > UMP_LENGTH) {
            key = key.substring(key.length() - UMP_LENGTH);
        }
        return Profiler.registerInfo(key, true, true);
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

    protected String getValidKey(String source) {
        return source.replaceAll(REGEX, SymbolEnum.UNDER_SCORE.getString());
    }
}
