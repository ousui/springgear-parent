package com.jd.springgear.beans;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jd.springgear.beans.interceptor.SpringGearInterceptor;
import com.jd.springgear.beans.interceptor.SpringGearInterceptorChain;
import com.jd.springgear.context.utils.SpringGearEngineUtils;
import com.jd.springgear.engine.SpringGearEngineInterface;
import com.jd.springgear.engine.annotation.SpringGearEngine;
import com.jd.springgear.exception.GearInterruptException;
import com.jd.springgear.exception.SpringGearException;
import com.jd.springgear.support.constants.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * SpringGear 的动态代理实现类。
 * 这里使用内部类直接实现。
 */
@Slf4j
public class SpringGearProxyInstance implements InvocationHandler, Serializable {

    private final ApplicationContext applicationContext;

    /**
     * 拦截器们
     */
    private final List<SpringGearInterceptor> interceptors;

    /**
     * bean 缓存
     */
    private Map<String, SpringGearEngineInterface> interfaceCachedMap = Maps.newConcurrentMap();

    public SpringGearProxyInstance(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        Map<String, SpringGearInterceptor> interceptorMap = this.applicationContext.getBeansOfType(SpringGearInterceptor.class);
        if (CollectionUtils.isEmpty(interceptorMap)) {
            interceptors = Collections.EMPTY_LIST;
        } else {
            interceptors = Lists.newArrayList(interceptorMap.values());
            AnnotationAwareOrderComparator.sort(interceptors);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 如果是对象，直接返回。
        // 这里是基于接口扫描并注册的类，所以这里基本不可能。
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(this, args);
        }

        // 记录日志。
        /**
         * 获取代理方法的注解。
         */
        SpringGearEngine engine = method.getAnnotation(SpringGearEngine.class);
        // 没有注解抛出异常，也可以考虑做一个默认的实现。
        if (null == engine) {
            log.error("You may be not use annotation '{}' for this method ''.", SpringGearEngine.class, method.getName());
            throw new UnsupportedOperationException(
                    String.format("class '%s' is an interface, you must use annotation '@%s' to implementation method '%s'.",
                            proxy.getClass().getSimpleName(), SpringGearEngine.class.getSimpleName(), method.getName())
            );
        }

        // 获取指定的 engine name
        String beanName = SpringGearEngineUtils.getInterfaceBeanName(engine, method);

        SpringGearEngineInterface engineBean = this.getEngineBean(beanName);

        log.debug("Engine bean is {}", engineBean.getClass());

        Object request = null;
        Object[] others = null; // 保证不为 Null，方便业务逻辑判断
        if (!ObjectUtils.isEmpty(args)) {
            log.debug("args is {}", args);
            if (null == args[0]) { // 第一个个入参为空，报空指针。
                throw new NullPointerException(
                        String.format("ApiWorkflowInterface's first argument must not be null, you can change the method '%s' without argument.", method.getName())
                );
            }

            // 入参第一个参数不是 request，则将其使用 ApiEntityWrapper 进行包裹进行传递。
//                if (!(ApiRequest.class.isAssignableFrom(args[0].getClass()))) {
//                    request = ApiEntityWrapper.build(args[0]);
//                } else {
//                }
            request = args[0];

            others = Arrays.copyOfRange(args, 1, args.length);
        }

        if (others == null) {
            others = new Object[0];
        }

        long timestamp = System.currentTimeMillis();


        SpringGearInterceptorChain chain = new SpringGearInterceptorChain(interceptors);

        Object resp = null;

        int code = HttpStatus.SC_OK;
        String msg = "ok";

        Exception ex = null;

        try {
            chain.beforeExecute(beanName, request, timestamp);

            resp = engineBean.execute(request, timestamp, others);

            chain.afterExecute(beanName, request, timestamp, null, resp);
        } catch (SpringGearException e) {
            ex = e;
            code = e.getCode();
            msg = e.getLocalizedMessage();
            if (e instanceof GearInterruptException) {
                resp = ((GearInterruptException) e).getResponse();
            }
            log.warn("There have some business exception happened, code: {}, msg: {}", code, msg);
            chain.onException(beanName, request, timestamp, null, resp, e);
        } catch (Exception e) {
            ex = e;
        } finally {
            chain.onFinally(beanName, request, timestamp, null, resp, ex);
        }

        Object result = engineBean.wrap(request, resp, timestamp, code, msg, others);
        log.debug("[{}] '{}' process context: {}", timestamp, beanName, result);
        return result;
    }

    /**
     * 从缓存池或者 application context 中查找 bean
     *
     * @param engineBeanName
     * @return
     */
    private SpringGearEngineInterface getEngineBean(String engineBeanName) {
        SpringGearEngineInterface face = interfaceCachedMap.get(engineBeanName);
        if (face != null) {
            return face;
        }
        Object bean = applicationContext.getBean(engineBeanName);

        if (null == bean) {
            log.error("not found this bean '{}'. ", engineBeanName);
            throw new NoSuchBeanDefinitionException(engineBeanName);
        }

        if (!SpringGearEngineInterface.class.isAssignableFrom(bean.getClass())) {
            log.error("Bean '{}' type '{}' is wrong, need type '{}'.", engineBeanName, bean.getClass(), SpringGearEngineInterface.class);
            throw new TypeMismatchException(bean, SpringGearEngineInterface.class);
        }

        face = (SpringGearEngineInterface) bean;

        // 将 bean 缓存，提高性能。
        interfaceCachedMap.put(engineBeanName, face);
        return face;
    }

}
