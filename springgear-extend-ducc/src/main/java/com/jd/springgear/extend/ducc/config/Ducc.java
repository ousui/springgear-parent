package com.jd.springgear.extend.ducc.config;

import com.jd.laf.config.ConfiguratorManager;
import com.jd.laf.config.spring.config.ConfigPostProcessor;
import com.jd.laf.config.spring.config.Observer;
import com.jd.laf.config.spring.config.PropertySourcesFactoryPostProcessor;
import com.jd.springgear.config.Properties;
import com.jd.springgear.extend.ducc.DuccConfigurator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import java.util.Map;

import static com.jd.laf.config.spring.config.Observer.OBSERVER_BEAN_NAME;

/**
 * ducc 支持，使用 config 直接 Import 该类，即可对系统集成 ducc。
 * ducc 的集成不阻碍系统 使用 spring 的 property source，系统默认为 ducc 优先级高于 spring
 *
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @see Import
 * @since 2020/12/19
 **/
@PropertySource(value = {
        "classpath:properties/ducc.properties", "classpath:ducc.properties",
}, ignoreResourceNotFound = true)
@Import({Properties.class})
public class Ducc implements EnvironmentAware, ApplicationContextAware {

    /**
     * ducc uri 模版
     */
    private final static String TPL_UCC_RESOURCE
            = "ucc://{username}:{token}@{host}/v1/namespace/{namespace}/config/{config}/profiles/{profiles}?longPolling={polling}&necessary={necessary}";

    /**
     * 获取 profiles 的前缀
     */
    private final static String PREFIX_PROPERTY_PROFILES = "ducc.profiles.";

    private Environment env;

    private ApplicationContext applicationContext;

    /**
     * ducc 配置器。
     * 参数使用 @Value 注入形式，由于 ConfiguratorManager 优先级较高，不能将入参放在 Config 类上，此时 Spring 还未进行替换查找操作。
     *
     * @param username
     * @param token
     * @param host
     * @param namespace
     * @param configs
     * @param polling
     * @param necessary
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public ConfiguratorManager configuratorManager(
            @Value("${ducc.username}") String username,
            @Value("${ducc.token}") String token,
            @Value("${ducc.host}") String host,
            @Value("${ducc.namespace}") String namespace,
            @Value("${ducc.configs}") String[] configs,
            @Value("${ducc.core.polling:60000}") long polling,
            @Value("${ducc.core.necessary:false}") boolean necessary
    ) {
        ConfiguratorManager manager = new ConfiguratorManager();
        manager.setApplication(username);

        for (String config : configs) {
            manager.addResource(
                    this.getResource(username, token, host, namespace, config, env.getProperty(PREFIX_PROPERTY_PROFILES.concat(config)), polling, necessary)
            );
        }

        Map<String, DuccConfigurator> configurators = applicationContext.getBeansOfType(DuccConfigurator.class);

        if (false == CollectionUtils.isEmpty(configurators)) {
            configurators.forEach((beanName, configurator) -> configurator.config(manager, applicationContext));
        }

        return manager;
    }

    /**
     * 监听器
     *
     * @return
     */
    @Bean(name = OBSERVER_BEAN_NAME)
    public static Observer observer() {
        return new Observer();
    }

    @Bean
    public static ConfigPostProcessor configPostProcessor() {
        return new ConfigPostProcessor();
    }

    @Bean
    public static PropertySourcesFactoryPostProcessor propertySourcesFactoryPostProcessor() {
        return new PropertySourcesFactoryPostProcessor();
    }

    /**
     * 获取 ducc resource
     *
     * @param username
     * @param token
     * @param host
     * @param namespace
     * @param config
     * @param profiles
     * @param polling
     * @param necessary
     * @return
     */
    private com.jd.laf.config.Resource getResource(String username, String token, String host, String namespace, String config, String profiles, long polling, boolean necessary) {

        String url = TPL_UCC_RESOURCE.replace("{username}", username)
                .replace("{token}", token)
                .replace("{host}", host)
                .replace("{namespace}", namespace)
                .replace("{config}", config)
                .replace("{profiles}", profiles)
                .replace("{polling}", String.valueOf(polling))
                .replace("{necessary}", String.valueOf(necessary));

        return new com.jd.laf.config.Resource(config, url);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
