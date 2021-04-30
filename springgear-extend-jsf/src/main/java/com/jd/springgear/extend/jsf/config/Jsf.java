package com.jd.springgear.extend.jsf.config;

import com.jd.jsf.gd.config.RegistryConfig;
import com.jd.jsf.gd.config.spring.ServerBean;
import com.jd.springgear.beans.AbstractSpringGearProxyProcessor;
import com.jd.springgear.context.SpringGearEngineProcessor;
import com.jd.springgear.extend.jsf.JsfProviderBeanDefinitionProcessor;
import com.jd.springgear.extend.jsf.engine.JsfSpringGearEngineExecutor;
import com.jd.springgear.extend.jsf.engine.ServerBeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2021/03/26
 **/
@PropertySource(value = {
        "classpath:jsf.properties",
        "classpath:properties/jsf.properties"
}, ignoreResourceNotFound = true)
public class Jsf {

    public static final String JSF_SERVER_BEAN_GROUP = "springgear.jsf.servers";

    @Bean
    public RegistryConfig jsfRegistry(
            @Value("${jsf.register.host:i.jsf.jd.com}") String host,
            @Value("${jsf.config.id:default-webapp}") String id
    ) {
        RegistryConfig config = new RegistryConfig();
        config.setId(id);
        config.setIndex(host);
        return config;
    }

    @Qualifier(JSF_SERVER_BEAN_GROUP)
    @Bean(destroyMethod = "destroy")
    public ServerBean firstJsfServer() {
        return ServerBeanUtils.newServerBean();
    }

    @Bean
    public SpringGearEngineProcessor springGearEngineProcessor() {
        return new SpringGearEngineProcessor(JsfSpringGearEngineExecutor.class);
    }
}
