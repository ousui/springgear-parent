package com.jd.springgear.context;

import com.jd.springgear.beans.AbstractSpringGearProxyProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * Spring Gear 框架配置入口
 * 杰夫 provider 自动扫描注册注册配置器。
 * 作为杰夫自动发布服务和自动创建 service 代理类的入口。
 * 类内执行顺序
 * 1. postProcessBeanDefinitionRegistry
 * 2. postProcessBeanFactory
 * 在这之前的 bean definition 可以查找到 ${} 类型的注解。 spring 会对注入的数据进行处理。
 * 在这之后定义的 bean definition 是找不到的，类已经完成了依赖分析。
 * 3. postProcessBeforeInitialization
 * 4. postProcessAfterInitialization
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2018-01-05
 **/
@Slf4j
public class SpringGearStarter implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware {

    /**
     * 需要扫描的包。
     */
    private String[] basePackages;

    /**
     * spring 上下文
     */
    private ApplicationContext applicationContext;


    private final AbstractSpringGearProxyProcessor beanDefinitionProcessor;

    public SpringGearStarter(AbstractSpringGearProxyProcessor beanDefinitionProcessor, String... basePackages) {
        Assert.notEmpty(basePackages, "必须注入使用包名！");
        this.basePackages = basePackages;
        this.beanDefinitionProcessor = beanDefinitionProcessor;
    }


    @Override
    public void afterPropertiesSet() {
        Assert.notEmpty(basePackages, "必须注入使用包名！");
    }

    /**
     * 处理修改接口自动实现，自动生成 jsf bean.
     *
     * @param beanDefinitionRegistry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        SpringGearProxyClassPathScanner scanner = new SpringGearProxyClassPathScanner(beanDefinitionRegistry, this.beanDefinitionProcessor);

        scanner.setResourceLoader(this.applicationContext);
        scanner.scan(basePackages);

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
//        保持空

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
