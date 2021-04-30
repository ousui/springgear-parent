package com.jd.springgear.extend.jsf.config;

import com.jd.jsf.gd.config.spring.ServerBean;
import com.jd.springgear.beans.AbstractSpringGearProxyProcessor;
import com.jd.springgear.extend.jsf.JsfProviderBeanDefinitionProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2021/03/26
 **/
@PropertySource(value = {
        "classpath:jsf.provider.properties",
        "classpath:properties/jsf.provider.properties",
}, ignoreResourceNotFound = true)
@Import(value = {
        Jsf.class
})
public class JsfProviders {

    @Bean
    public AbstractSpringGearProxyProcessor springGearProxyProcessor(@Qualifier(Jsf.JSF_SERVER_BEAN_GROUP) List<ServerBean> serverBeans) {
        return new JsfProviderBeanDefinitionProcessor(serverBeans);
    }

}
