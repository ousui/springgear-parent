package com.jd.springgear.extend.jsf;

import com.jd.springgear.extend.jsf.annotation.JsfParameter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * jsf 服务客户端注解处理。
 * 优先使用类型。
 * 该注解只注解字段，不对字段做注入处理，只是做统一注解处理。
 * 使用 Value, Qualifier 等是为了方便 IDEA 可以方便点击跳转。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2018-01-23
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsfConsumer {

    /**
     * bean name, 未指定的话，默认使用 jsf.consumer.{fieldName} 来定义。
     *
     * @return
     */
    String name() default "";

    /**
     * 杰夫别名，必填
     *
     * @return
     */
    Value alias();

    /**
     * 超时时间
     *
     * @return
     */
    Value timeout() default @Value("${jsf.consumer.timeout:5000}");

    /**
     * 失败后重试次数
     */
    Value retries() default @Value("${jsf.consumer.retries:0}");

    /**
     * 是否强依赖服务端
     *
     * @return
     */
    Value check() default @Value("${jsf.consumer.check:false}");

    /**
     * 负载策略
     *
     * @return
     */
    Value loadbalance() default @Value("${jsf.consumer.loadbalance:random}");

    /**
     * 是否异步调用
     */
    Value async() default @Value("${jsf.consumer.async:false}");

    /**
     * 过滤器引用
     * filter reference
     *
     * @return
     */
    Qualifier[] filters() default {};


    /**
     * jsf 隐藏参数
     *
     * @return
     */
    JsfParameter[] parameters() default {};

    /**
     * 序列化形式：hessian，msgpack
     */
    Value serialization() default @Value("${jsf.consumer.serialization:msgpack}");

}
