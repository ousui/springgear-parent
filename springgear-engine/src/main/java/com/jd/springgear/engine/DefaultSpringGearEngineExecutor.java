package com.jd.springgear.engine;

import com.jd.springgear.exception.GearInterruptException;
import lombok.extern.slf4j.Slf4j;

/**
 * AbstractSpringGearEngineExecutor 的默认实现
 *
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2020/12/13
 **/
@Slf4j
public class DefaultSpringGearEngineExecutor extends AbstractSpringGearEngineExecutor {

    private final static String SOURCE = "DEFAULT";

    @Override
    protected String getSource() throws GearInterruptException {

        return SOURCE;
    }
}
