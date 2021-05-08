package com.jd.springgear.extend.ump.annotation;

import com.jd.springgear.support.enums.SymbolEnum;
import com.jd.ump.profiler.proxy.Profiler;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2021/05/08
 **/
public class UmpRegisterInterceptor extends SpringGearUmpInterceptor implements Ordered {

    private final int order;
    private final String appname;

    public UmpRegisterInterceptor() {
        // 获取 app name
        this(Ordered.LOWEST_PRECEDENCE, getAppname());
    }

    public UmpRegisterInterceptor(String appname) {
        this(Ordered.LOWEST_PRECEDENCE, appname);
    }

    public UmpRegisterInterceptor(int order, String appname) {
        Assert.hasText(appname, "需要填写应用名");
        this.order = order;
        this.appname = appname;

        String systemKey = "heartbeat".concat(SymbolEnum.DOT.getString()).concat(appname);
        String jvmKey = "jvm".concat(SymbolEnum.DOT.getString()).concat(appname);
        Profiler.InitHeartBeats(systemKey);
        Profiler.registerJVMInfo(jvmKey);
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    /**
     * 获取应用名
     *
     * @return
     */
    private static String getAppname() {
        final String propertyName = "deploy.app.name";
        String name = System.getenv(propertyName);
        if (false == StringUtils.hasText(name)) {
            name = System.getProperty(propertyName, "");
        }
        return name;
    }
}
