package com.jd.springgear.extend.orika.config;

import com.jd.springgear.extend.orika.OrikaBeanMapper;
import ma.glasnost.orika.MapperFacade;
import org.springframework.context.annotation.Bean;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2021/03/30
 **/
public class Orika {

    @Bean
    public MapperFacade mapperFacade() {
        return new OrikaBeanMapper();
    }

}
