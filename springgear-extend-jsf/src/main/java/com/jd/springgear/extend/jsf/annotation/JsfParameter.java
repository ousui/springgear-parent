package com.jd.springgear.extend.jsf.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Jsf 配置参数
 *
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>) 2018-06-22
 **/
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsfParameter {

    /**
     * jsf key
     *
     * @return key
     */
    String key();

    /**
     * jsf value
     *
     * @return value
     */
    String value();

    /**
     * jsf 参数是否为隐藏的。
     *
     * @return true/false
     */
    boolean hidden() default false;
}
