package com.jd.springgear.engine.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注本注解表示将 service 生成 spring gear 代理类
 *
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>) 2018-01-05
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface SpringGearProxy {

    /**
     * 该组件的 bean 默认生成的 bean name。
     *
     * @return 代理值
     */
    String value() default "";
}
