package com.jd.springgear.beans;

import com.jd.springgear.engine.annotation.SpringGearProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * jsf provider bean 处理器
 * 1. scan 期间修改 接口 类型的 bean，为其进行自动代理。
 * 2. scan 期间生成 jsf bean(支持属性注入)
 * 2. before init 期间，生成 workflow 的支持类。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2018-01-08
 **/
@Slf4j
public abstract class AbstractSpringGearProxyProcessor implements ApplicationContextAware {

    private ApplicationContext applicationContext;


    /**
     * 构建描述文件
     *
     * @param targetClass 目标类，一般为接口
     * @param beanName    beanName
     * @param definition
     * @return
     */
    protected abstract BeanDefinition buildBeanDefinition(Class targetClass, String beanName, GenericBeanDefinition definition);

    /**
     * 获取需要注册的 bean name
     *
     * @param originBeanName 原始 bean name
     * @return
     */
    protected abstract String getRegisterBeanName(String originBeanName);

    /**
     * 批量处理
     *
     * @param holders
     */
    public void autoProxyInterface(Collection<BeanDefinitionHolder> holders) {
        for (BeanDefinitionHolder holder : holders) {
            this.autoProxyInterface(holder);
        }
    }

    /**
     * 构建方法。
     * 算是一个入口。
     *
     * @param holder
     */
    public void autoProxyInterface(BeanDefinitionHolder holder) {
        GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
        String targetBeanClassName = definition.getBeanClassName();
        String beanName = holder.getBeanName();

        Class proxyInterface;
        try {
            proxyInterface = Class.forName(targetBeanClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        SpringGearProxy proxyAnnotation = (SpringGearProxy) proxyInterface.getAnnotation(SpringGearProxy.class);
        if (proxyAnnotation == null) {
            log.info("this component is not '{}', don't process.", SpringGearProxy.class);
            return;
        }

        /**
         * 构建 bean 描述文件
         */
        BeanDefinition beanDefinition = this.buildBeanDefinition(proxyInterface, beanName, definition);

        if (beanDefinition == null) {
            return;
        }

        String registerBeanName = this.getRegisterBeanName(beanName);
        /**
         * 注册 bean 描述文件
         */
        this.registerBeanDefinition(StringUtils.hasText(registerBeanName) ? registerBeanName : beanName, beanDefinition);
    }

    /**
     * 注册 bean definition
     *
     * @param name
     * @param beanDefinition
     */
    private void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        log.info("Creating bean with name '{}' for '{}'.", name, beanDefinition.getBeanClassName());

        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        beanFactory.registerBeanDefinition(name, beanDefinition);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
