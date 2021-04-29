package com.jd.springgear.beans;

import com.jd.springgear.beans.factory.SpringGearProxyFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2020/12/13
 **/
@Slf4j
public class DefaultBeanDefinitionProcessor extends AbstractSpringGearProxyProcessor {

    @Override
    protected BeanDefinition buildBeanDefinition(Class targetClass, String beanName, GenericBeanDefinition definition) {

        log.debug(
                String.format("Creating Spring Gear FactoryBean with name '%s' and '%s' Spring Gear Interface",
                        beanName, definition.getBeanClassName())
        );

        /**
         * 放入两个构造参数
         */
        definition.getConstructorArgumentValues().addGenericArgumentValue(targetClass);

        definition.setBeanClass(SpringGearProxyFactoryBean.class);

        definition.applyDefaults(new BeanDefinitionDefaults());

        /**
         * 默认按照类型注入
         */
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

        return null;
    }

    @Override
    protected String getRegisterBeanName(String originBeanName) {
        return originBeanName;
    }
}
