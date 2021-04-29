package com.jd.springgear.extend.jsf.engine;

import com.jd.jsf.gd.config.spring.ServerBean;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2020/12/13
 **/
public class ServerBeanUtils {

    public static ServerBean<?> newServerBean() {
        return new SpringGearJsfServerBean();
    }

    static class SpringGearJsfServerBean extends ServerBean<Object> {
    }

}
