package com.jd.springgear.webmvc.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 默认是在 ResponseBodyWrapperAdvice 类中使用，默认为加本注解，额外配置主要是为了查询 false 状态，而不进行封装。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2017-12-19
 * @see ResponseBodyWrapperAdvice
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBodyWrapper {

    /**
     * 是否需要进行 wrapper
     * @return
     */
    boolean required() default true;
}
