package com.jd.springgear.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2020/12/19
 **/
// 自动查找路径
@PropertySource(value = {
        "classpath:common.properties",
        "classpath:spring.properties",
        "classpath:properties/common.properties",
        "classpath:properties/spring.properties",
}, ignoreResourceNotFound = true)
public class Properties {
    /**
     * 属性配置器，用于加载属性配置
     *
     * @return
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
