package com.jd.springgear.extend.jsf;

import com.google.common.collect.Lists;
import com.jd.jsf.gd.config.spring.ProviderBean;
import com.jd.jsf.gd.config.spring.ServerBean;
import com.jd.springgear.beans.AbstractSpringGearProxyProcessor;
import com.jd.springgear.beans.factory.SpringGearProxyFactoryBean;
import com.jd.springgear.extend.jsf.annotation.JsfProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.util.List;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2020/12/13
 **/
@Slf4j
public class JsfProviderBeanDefinitionProcessor extends AbstractSpringGearProxyProcessor {
    /**
     * jsf server beans
     */
    private final List<ServerBean> jsfServerBean;

    private final static String PREFIX_JSF_PROVIDER_BEAN = "jsf.provider.";

    public JsfProviderBeanDefinitionProcessor(List<ServerBean> jsfServerBean) {
        this.jsfServerBean = jsfServerBean;
    }

    public JsfProviderBeanDefinitionProcessor(ServerBean jsfServerBean) {
        this.jsfServerBean = Lists.newArrayList(jsfServerBean);
    }

    @Override
    protected BeanDefinition buildBeanDefinition(Class targetClass, String beanName, GenericBeanDefinition definition) {

        log.debug("Creating Spring Gear FactoryBean with name '{}' and '{}' Spring Gear Interface", beanName, definition.getBeanClassName());

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

        JsfProvider annotation = (JsfProvider) targetClass.getAnnotation(JsfProvider.class);
        if (annotation == null) {
            return definition;
        }

        Value alias = annotation.alias();
        Value timeout = annotation.timeout();
        Value dynamic = annotation.dynamic();
        Value weight = annotation.weight();

        BeanDefinition jsfProvider = BeanDefinitionBuilder.genericBeanDefinition(ProviderBean.class)
                .addPropertyValue("interfaceId", targetClass.getName())
                .addPropertyValue("server", jsfServerBean)
                .addPropertyValue("ref", definition)
                .addPropertyValue("timeout", timeout.value())
                .addPropertyValue("alias", alias.value())
                .addPropertyValue("weight", weight.value())
                .addPropertyValue("dynamic", dynamic.value()).getBeanDefinition();

        return jsfProvider;
    }

    @Override
    protected String getRegisterBeanName(String originBeanName) {
        return PREFIX_JSF_PROVIDER_BEAN.concat(originBeanName);
    }
}
