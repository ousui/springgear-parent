package com.jd.springgear.webmvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * api 接口的权限认证注解。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2017-12-19
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Authentication {

    /**
     * 是否需要权限认证，标注注解后，默认为 true。
     * @return t/f
     */
    boolean required() default true;
}
