package com.jd.springgear.extend.ump.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 监控部分注解配置
 * 可以定义在 method 上和 {@link SpringGearEngine} 中，以外部作为优先使用。
 *
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>) 2018-05-30
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UmpMonitor {
    /**
     * 是否启用 ump 监控
     *
     * @return true/false
     */
    boolean enabled() default true;

    /**
     * ump 配置的 key
     *
     * @return ump key
     */
    String key() default "";

//    String application() default "";

    /**
     * 是否启用方法心跳监控
     *
     * @return true/false
     */
    boolean heartbeat() default false;

    /**
     * 是否启用方法性能监控
     *
     * @return true/false
     */
    boolean performance() default true;
}
