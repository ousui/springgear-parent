package com.jd.springgear.extend.orika;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.ClassMapBuilder;

/**
 * custom 的扩展，支持添加 fields 进行自动注册。
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2018-01-18
 **/
@Slf4j
public abstract class OrikaCustomMapper<A, B> extends CustomMapper<A, B> {

    /**
     * 来做一些需要扩展的事情，供外部调用，不可复写。
     *
     * @param builder ClassMapBuilder
     */
    public abstract void beforeRegister(ClassMapBuilder<A, B> builder);

    public abstract void mapAtoB(A a, B b) throws Exception;

    public abstract void mapBtoA(B b, A a) throws Exception;

    @SneakyThrows
    @Override
    public final void mapAtoB(A a, B b, MappingContext context) {
        if (log.isDebugEnabled()) {
            log.debug("orika A - B mapping input(A): {}, source output(B): {}", a, b);
        }
        try {
            this.mapAtoB(a, b);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (log.isDebugEnabled()) {
            log.debug("orika A - B mapping input(A): {}, after mapping output(B): {}", a, b);
        }
    }

    @Override
    public final void mapBtoA(B b, A a, MappingContext context) {
        if (log.isDebugEnabled()) {
            log.debug("orika B - A mapping input(B): {}, source output(A): {}", b, a);
        }
        try {
            this.mapBtoA(b, a);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (log.isDebugEnabled()) {
            log.debug("orika B - A mapping input(B): {}, after mapping output(A): {}", b, a);
        }
    }
}
