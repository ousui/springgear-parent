package com.jd.springgear.engine.context;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * 上下文用于传递数据。如果有复杂业务可以继承 Context 添加获取 parameter 逻辑
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2018-01-10
 **/
@ToString
public class SpringGearContext<REQ, RESP> implements Serializable, Cloneable {

    /**
     * 入参，只读
     */
    @Getter
    private final REQ request;

    /**
     * 出参，读写
     */
    @Getter
    @Setter
    private RESP response;

    /**
     * 来源，只读
     */
    @Getter
    private final String source;

    /**
     * 时间戳，只读
     */
    @Getter
    private final long timestamp;

    /**
     * 用于参数传递
     */
//    @JsonProperty
    private final Map<String, Object> parameters;

    public SpringGearContext(REQ request, String source, long timestamp) {
        this.request = request;
        this.source = source;
        this.timestamp = timestamp;
        this.parameters = Maps.newHashMap();
    }


    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public SpringGearContext<REQ, RESP> setParameter(String key, Object value) {
        parameters.put(key, value);
        return this;
    }

    public <T> T getParameter(String key, T defaultValue) {
        Optional<T> value = (Optional<T>) Optional.fromNullable(parameters.get(key));
        return value.or(defaultValue);
    }

    public <T> T getParameter(String key) {
        Optional<T> value = (Optional<T>) Optional.fromNullable(parameters.get(key));
        return value.orNull();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        SpringGearContext<REQ, RESP> context = (SpringGearContext<REQ, RESP>) super.clone();
        context.parameters.putAll(Maps.newHashMap(this.parameters));
        return context;
    }
}
