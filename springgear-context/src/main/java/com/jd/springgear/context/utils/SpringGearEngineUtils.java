package com.jd.springgear.context.utils;

import com.google.common.collect.Multimap;
import com.jd.springgear.engine.annotation.SpringGearEngine;
import com.jd.springgear.support.enums.SymbolEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2020/12/13
 **/
@Slf4j
public class SpringGearEngineUtils {

    /**
     * 代理类 bean 的前缀
     */
    public final static String INTERFACE_PREFIX = "springgear.";

    /**
     * 按照 method 获取接口定义的 bean name，规则：
     * springgear . interface 类的 simple class name # method name
     * 例如： springgear.springGearInterface#method1
     *
     * @param engine SpringGearEngine
     * @param method 默认调用的  method
     * @return bean name
     */
    public static String getInterfaceBeanName(SpringGearEngine engine, Method method) {
        if (engine != null && StringUtils.hasText(engine.name())) {
            return engine.name();
        }
        String clazz = method.getDeclaringClass().getSimpleName();
        return INTERFACE_PREFIX.concat(clazz).concat(SymbolEnum.HASH.getString()).concat(method.getName());
    }

    public static void registerBeanDefinition(ApplicationContext applicationContext, String name, BeanDefinition beanDefinition) {
        log.info("Creating bean with name '{}' for '{}'.", name, beanDefinition.getBeanClassName());

        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        beanFactory.registerBeanDefinition(name, beanDefinition);
    }

    public static <T> void groupBeanByQualifier(ApplicationContext applicationContext, Class<T> clazz, Multimap<String, T> map, Function<Class<T>, Qualifier> function) {
        Map<String, T> classMap = applicationContext.getBeansOfType(clazz);
        if (CollectionUtils.isEmpty(classMap)) {
            return;
        }

        if (function == null) {
            return;
        }

        classMap.values().forEach(bean -> {
            Qualifier group = function.apply((Class<T>) bean.getClass());
            if (group == null) {
                return;
            }
            map.put(group.value(), bean);
        });
    }

}
