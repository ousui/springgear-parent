package com.jd.springgear.engine.handler;

import com.jd.springgear.engine.context.SpringGearContext;
import org.springframework.core.Ordered;

/**
 * 抽象实现 AbstractApiWorkflowHandler，对工程结尾做一个了断。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2017-12-13
 **/
public abstract class AbstractSpringGearEndingHandler<REQ, RESP> extends AbstractSpringGearHandler<REQ, RESP> {

    public abstract RESP end(SpringGearContext<REQ, RESP> context, REQ req, Object... others) throws Exception;

    @Override
    public void handle(SpringGearContext<REQ, RESP> context, Object... others) throws Exception {
        RESP resp = this.end(context, context.getRequest(), others);
        context.setResponse(resp);
    }

    @Override
    public final int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }


}
