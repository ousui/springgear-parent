package com.jd.springgear.extend.jsf;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jd.jsf.gd.config.spring.ConsumerBean;
import com.jd.springgear.extend.jsf.annotation.JsfParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 杰夫 consumer 基础处理类。</br >
 * 所有由继承本类生成的 bean ，均以 'jsf.consumer.' 为开头。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2018-01-23
 **/
@Slf4j
@Lazy
public abstract class JsfConsumerBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor {

    private final static String PREFIX_JSF_CONSUMER_BEAN = "jsf.consumer.";

    @Override
    public final void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        List<Field> fields = Lists.newArrayList();
        // 递归获取 class
        this.getFieldsRecursive(this.getClass(), fields);

        for (Field field : fields) {
            this.buildJsfConsumerBeanDefinition(registry, field);
        }
    }

    /**
     * 递归获取所有字段
     *
     * @param tClass
     * @param fields
     */
    private void getFieldsRecursive(Class tClass, List<Field> fields) {
        if (tClass == JsfConsumerBeanDefinitionRegistry.class) {
            return;
        }
        Field[] fieldArray = tClass.getDeclaredFields();

        for (Field field : fieldArray) {
            fields.add(field);
        }
        this.getFieldsRecursive(tClass.getSuperclass(), fields);
    }

    /**
     * 创建 jsf consumer bean definition
     *
     * @param registry
     * @param field
     */
    private void buildJsfConsumerBeanDefinition(BeanDefinitionRegistry registry, Field field) {

        JsfConsumer annotation = field.getAnnotation(JsfConsumer.class);
        if (annotation == null) { // 如果空，则返回。
            return;
        }

        String beanName = annotation.name();
        String clazz = field.getType().getName();
        if (!StringUtils.hasText(beanName)) {
            beanName = PREFIX_JSF_CONSUMER_BEAN.concat(field.getName());
        }

        Value alias = annotation.alias();
        Value check = annotation.check();
        Value async = annotation.async();
        Value loadbalance = annotation.loadbalance();
        Value retries = annotation.retries();
        Value timeout = annotation.timeout();
        Value serialization = annotation.serialization();
        // 暂未实现
        Qualifier[] filters = annotation.filters();
        JsfParameter[] parameters = annotation.parameters();


        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ConsumerBean.class)
                .addPropertyValue("interfaceId", clazz)
                .addPropertyValue("check", check.value())
                .addPropertyValue("async", async.value())
                .addPropertyValue("loadbalance", loadbalance.value())
                .addPropertyValue("retries", retries.value())
                .addPropertyValue("timeout", timeout.value())
                .addPropertyValue("serialization", serialization.value())
                .addPropertyValue("alias", alias.value());


        Map<String, String> parameterMap = Maps.newHashMap();

        // 处理 parameters
        if (parameters != null && parameters.length > 0) {
            for (JsfParameter parameter : parameters) {
                if ("".equals(parameter.key()) && "".equals(parameter.value())) {
                    continue;
                }
                parameterMap.put(
                        (parameter.hidden() ? "." : "").concat(parameter.key()), // 如果 hidden 属性为 true，则添加 . 前缀。
                        parameter.value());
            }

            builder.addPropertyValue("parameters", parameterMap);
        }

        // 处理 filters
        if (filters != null && filters.length > 0) {
            ManagedList filterList = new ManagedList(filters.length);
            for (Qualifier filter : filters) {
                String value = filter.value();
                if (!StringUtils.hasText(value)) {
                    log.warn("Can't found filter with empty name for Bean '{}'.", beanName);
                    continue;
                }

                BeanDefinition filterBean = registry.getBeanDefinition(value);
                if (filterBean.isSingleton()) {
                    log.warn("If custom filter:\"{}\" used by multiple provider/consumer, you need to set attribute scope=\"property\"!", value);
                }

                filterList.add(new RuntimeBeanReference(value));
            }
            builder.addPropertyValue("filter", filterList);
        }
        BeanDefinition bean = builder.getBeanDefinition();
        // 获取本类 lazy
        Lazy lazy = field.getAnnotation(Lazy.class);
        if (lazy == null) {
            lazy = this.getClass().getAnnotation(Lazy.class);
        }
        bean.setLazyInit(lazy != null && lazy.value()); // 默认使用 lazy 加载。

        log.info("Creating JSF consumer bean with name '{}' for class '{}'.", beanName, clazz);

        registry.registerBeanDefinition(beanName, bean);
    }

    @Override
    public final void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}
