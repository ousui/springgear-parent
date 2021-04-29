package com.jd.springgear.engine.handler;

import com.jd.springgear.engine.context.SpringGearContext;
import com.jd.springgear.exception.GearInterruptException;
import com.jd.springgear.support.constants.HttpStatus;
import org.springframework.core.Ordered;

/**
 * AbstractSpringGearHandler 的 有一层抽象，主要是实现了验证方法。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2017-12-13
 **/
public abstract class AbstractSpringGearRequestValidHandler<REQ, RESP> extends AbstractSpringGearHandler<REQ, RESP> {

    public abstract void verify(REQ request, Object... others) throws IllegalArgumentException;

    @Override
    public void handle(SpringGearContext<REQ, RESP> context, Object... others) throws GearInterruptException {
        try {
            this.verify(context.getRequest(), others);
        } catch (IllegalArgumentException e) {
            this.throwInterruptException(HttpStatus.SC_BAD_REQUEST, "参数验证错误：{}。", e.getLocalizedMessage());
        }
    }


    @Override
    public final int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10000;
    }


}
