package com.jd.springgear.extend.ducc;

import com.jd.laf.config.ConfiguratorManager;
import com.jd.laf.config.spring.config.JavaScriptListener;
import org.springframework.context.ApplicationContext;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2020/12/19
 **/
public abstract class JavaScriptDuccConfigurator implements DuccConfigurator {

    private final String key;

    public JavaScriptDuccConfigurator(String key) {
        this.key = key;
    }

    @Override
    public void config(ConfiguratorManager manager, ApplicationContext context) {
        manager.addListener(new JavaScriptListener(this.key, this.getScriptPath(), context));
    }

    protected abstract String getScriptPath();


}
