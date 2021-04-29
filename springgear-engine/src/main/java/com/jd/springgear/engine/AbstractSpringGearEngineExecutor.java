package com.jd.springgear.engine;

import com.google.common.eventbus.AsyncEventBus;
import com.jd.springgear.engine.context.NullRequest;
import com.jd.springgear.engine.context.SpringGearContext;
import com.jd.springgear.engine.context.SpringGearResultOriginalWrapper;
import com.jd.springgear.engine.context.SpringGearResultWrapper;
import com.jd.springgear.engine.handler.SpringGearHandler;
import com.jd.springgear.eventbus.SpringGearEventListener;
import com.jd.springgear.eventbus.annotation.SpringGearEvent;
import com.jd.springgear.exception.GearContinueException;
import com.jd.springgear.exception.GearInterruptException;
import com.jd.springgear.exception.SpringGearException;
import com.jd.springgear.support.constants.HttpStatus;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * pipeline 的抽象实现
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2017-12-13
 **/
@Slf4j
public abstract class AbstractSpringGearEngineExecutor implements SpringGearEngineInterface, BeanNameAware, InitializingBean {

    @Setter
    private String beanName;

    /**
     * 使用的 handlers
     */
    @Setter
    private List<SpringGearHandler<?, ?>> handlers;

    /**
     * 要注册的监听器
     */
    @Setter
    private List<SpringGearEventListener> listeners;

    @Setter
    private SpringGearResultWrapper wrapper;

    /**
     * eventbus
     */
    private AsyncEventBus eventBus = null;


    /**
     * 获取请求来源
     *
     * @return
     */
    protected abstract String getSource() throws GearInterruptException;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.initResultWrapper();
        this.initEventBus();
    }

    /**
     * 初始化 result wrapper
     */
    private void initResultWrapper() {
        if (wrapper != null) {
            return;
        }
        wrapper = new SpringGearResultOriginalWrapper();
    }

    /**
     * 初始化 eventBus
     */
    private void initEventBus() {
        if (CollectionUtils.isEmpty(listeners)) {
            return;
        }

        eventBus = new AsyncEventBus(beanName, Executors.newFixedThreadPool(5));
        listeners.forEach(eventBus::register);
    }

    @Override
    public Object execute(Object request, long timestamp, Object... others) throws SpringGearException {
        if (log.isDebugEnabled()) {
            log.debug("Springgear engine for bean `{}` start execute main process with handlers: {}", this.beanName, this.handlers);
        }
        if (CollectionUtils.isEmpty(handlers)) {
            log.warn("There is no handlers ware injected to bean `{}`, please check.", this.beanName);
            return null;
        }

        if (request == null) {
            request = NullRequest.INSTANCE;
        }

        String source = this.getSource();
        SpringGearContext context = new SpringGearContext(request, source, timestamp);

        if (log.isDebugEnabled()) { // 入参记录，方便问题跟踪，只记录一次就好。
            log.debug("TS[{}-{}] Handler '{}' start work. request is: {}", source, timestamp, beanName, request);
        }

        /**
         * 核心 handlers 循环处理
         */
        for (SpringGearHandler<?, ?> handler : handlers) {

            if (!handler.supports(context, others)) { // 如果不支持，则 continue。
                continue;
            }

            // event 获取
            SpringGearEvent sge = handler.getClass().getAnnotation(SpringGearEvent.class);
            Throwable ex = null;
            try {
                handler.handle(context, others);
            } catch (Throwable e) { // 捕获并处理异常
                ex = e;
                this.handleThrowable(context, e);
            } finally {
                long duration = System.currentTimeMillis() - timestamp;

                if (log.isDebugEnabled()) {
                    log.debug("TS[{}-{}] Handler '{}#{}' finished work. duration {} ms. context is: {}",
                            source, timestamp, handler.getClass().getSimpleName(), handler.getOrder(), duration, context);
//                            source, timestamp, handler.getClass().getSimpleName(), handler.getOrder(), duration, context);
                }

                if (eventBus != null && sge != null) {
                    if (sge.copied()) {
                        // TODO
                        //clone 对象，防止地址变更对数据的影响
                    }
                    log.debug("TS[{}-{}] Handler '{}#{}' has event, do post: {}", source, timestamp, handler.getClass().getSimpleName(), handler.getOrder(), context);
                    eventBus.post(new Object[]{
                            context, ex, handler.getClass()
                    });
                }

            }

        }

        long duration = System.currentTimeMillis() - timestamp;

        // 记录一次 context，方便线上问题排查
        if (log.isInfoEnabled()) {
            log.info("TS[{}-{}] Handler '{}' finished work. duration {} ms. context is: {}", source, timestamp, beanName, duration, context);
        }

        final Object response = context.getResponse();
        return response;
    }

    @Override
    public Object wrap(Object o, long timestamp, int code, String msg, Object... others) {

        if (log.isDebugEnabled()) {
            log.debug("start wrap parameters, o: `{}`, timestamp: `{}`, code: `{}`, msg: `{}`", o, timestamp, code, msg);
        }

        return wrapper.process(o, timestamp, code, msg, others);
    }

    /**
     * 针对各种异常的处理。
     * TODO 将异常及对应的 code 做成 mapping 形式，进行自动化。
     *
     * @param context
     * @param e
     * @throws SpringGearException
     */
    private void handleThrowable(SpringGearContext<?, ?> context, Throwable e) throws SpringGearException {
        String source = context.getSource();
        long timestamp = context.getTimestamp();
        Object response = context.getResponse();
        // 打印堆栈
        String localizedMessage = e.getLocalizedMessage();

        // TODO
        String logMessage = String.format("TS[%s-%s] %s：%s | %s | STACK TRACE: ",
                source, timestamp, e.getClass().getSimpleName(), localizedMessage, context);

        if (!(e instanceof SpringGearException)) {
            log.error(logMessage, e);
            int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;

            if (e instanceof IllegalArgumentException) { // 参数一场，标记 status
                status = HttpStatus.SC_BAD_REQUEST;
                // TODO
//            } else if (e instanceof RpcException) { // RPC 调用一场，标记 status
//                status = HttpStatus.SC_BAD_GATEWAY;
//                localizedMessage = "系统异常，请重试或联系客服[JSF]";
            } else if (e instanceof NullPointerException) { // 空指针异常，给一个友好文案。
                localizedMessage = "系统异常，请重试或联系客服[N]";
            } else if (e instanceof SocketTimeoutException || e instanceof TimeoutException) { // 超时异常
                localizedMessage = "系统异常，请重试或联系客服[T]";
            } else {
                localizedMessage = "系统异常，请重试或联系客服[Z]";
            }
            // 异常抛出
            throw new SpringGearException(localizedMessage, status, timestamp);
        }

        ((SpringGearException) e).setTimestamp(timestamp);

        if (e instanceof GearContinueException) {// 捕获到 continue 异常，返回继续向下执行代码。
            log.warn(logMessage);
//            log.warn(logMessage, e);
            return;
        } else if (e instanceof GearInterruptException) {
            switch (((GearInterruptException) e).getCode()) { // 以下几种类型不记录堆栈
                /** @see HttpResponseStatus **/
                case HttpStatus.SC_OK:
                case HttpStatus.SC_UNAUTHORIZED:
                case HttpStatus.SC_NOT_FOUND:
                    log.error(logMessage);
                    break;
                default:
                    log.error(logMessage, e);
                    break;
            }
            if (response != null && ((GearInterruptException) e).getResponse() == null) { // 如果是 中断异常，且 response 没有内容，尝试将 response 放入。
                ((GearInterruptException) e).setResponse(response);
            }
        } else {
            log.error(logMessage, e);
        }

        throw (SpringGearException) e;
    }


}
