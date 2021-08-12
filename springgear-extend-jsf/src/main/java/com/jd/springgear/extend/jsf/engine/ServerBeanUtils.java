package com.jd.springgear.extend.jsf.engine;

import com.jd.jsf.gd.config.spring.ServerBean;
import com.jd.jsf.gd.util.Constants;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2020/12/13
 **/
public class ServerBeanUtils {

    public static ServerBean<?> newServerBean() {
        return newServerBean(Constants.CodecType.hessian);
    }

    public static ServerBean<?> newServerBean(Constants.CodecType type) {
        ServerBean bean = new SpringGearJsfServerBean();
        bean.setSerialization(type.name());
        return bean;
    }

    static class SpringGearJsfServerBean extends ServerBean<Object> {
    }

}
