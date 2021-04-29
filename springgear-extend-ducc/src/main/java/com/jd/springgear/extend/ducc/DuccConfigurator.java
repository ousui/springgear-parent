package com.jd.springgear.extend.ducc;

import com.jd.laf.config.ConfiguratorManager;
import org.springframework.context.ApplicationContext;

/**
 * ducc 配置器，作为 ducc 配置的扩展进行手动配置，提供了更加灵活的扩展能力
 *
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2020/12/19
 **/
public interface DuccConfigurator {

    /**
     * 加入 spring 上下文的扩展
     *
     * @param manager
     * @param context
     */
    void config(ConfiguratorManager manager, ApplicationContext context);

}
