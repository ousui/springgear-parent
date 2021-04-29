package com.jd.springgear.extend.jsf.annotation;

import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * spring jsf provider 注册注解
 * <p>
 * 使用 @Value 注解可以直接被处理器进行解析处理，同时也支持 IDE 跳转。
 *
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>) 2018-01-05
 * @see Value
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsfProvider {
    /**
     * 杰夫别名
     *
     * @return Value
     */
    Value alias();

    /**
     * 超时时间
     *
     * @return Value
     */
    Value timeout() default @Value("${jsf.provider.timeout:5000}");

    /**
     * 权重
     *
     * @return Value
     */
    Value weight() default @Value("${jsf.provider.weight:100}");

    /**
     * 是否动态
     *
     * @return Value
     */
    Value dynamic() default @Value("${jsf.provider.dynamic:true}");

}
