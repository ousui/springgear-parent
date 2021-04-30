package com.jd.springgear.beans.factory;

import com.jd.springgear.beans.SpringGearProxyInstance;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Spring Gear 动态代理 工厂类，用于动态代理生成 接口实现类。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2018-01-06
 **/
@Slf4j
public class SpringGearProxyFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware, BeanNameAware {

    private final Class<T> interfaceProxy;

    @Setter
    private ApplicationContext applicationContext;

    @Getter
    @Setter
    private String beanName;


    /**
     * spring 会调用构造方法将 接口类型传入。
     *
     * @param interfaceProxy
     */
    private SpringGearProxyFactoryBean(Class<T> interfaceProxy) {
        this.interfaceProxy = interfaceProxy;
    }


    @Override
    public T getObject() {
        /**
         * 新建代理实例。
         */
        SpringGearProxyInstance instance = new SpringGearProxyInstance(applicationContext);
        /**
         * 这里直接使用 jdk 的动态代理实现，性能损耗较大，但是考虑到这里只在初始化的时候使用，忽略掉性能损耗。
         * 调优可以考虑使用 spring 的 代理实现。
         */
        return (T) Proxy.newProxyInstance(this.interfaceProxy.getClassLoader(), new Class[]{this.interfaceProxy}, instance);
    }

    @Override
    public Class<T> getObjectType() {
        return interfaceProxy;
    }

    @Override
    public boolean isSingleton() {
        // 强制使用单例
        return true;
    }

}
