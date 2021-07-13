package com.jd.springgear.engine.context;

/**
 * 出参处理接口
 * <p>
 * 系统会自动寻找命名为 springgear.wrapper.default 的默认处理器作为结果返回处理器
 *
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2021/03/25
 **/
public interface SpringGearResultWrapper<R> {

    /**
     * 默认 bean name，获取不到的话，则使用 original
     */
    String DEFAULT_BEAN_NAME = "springgear.wrapper.default";

    /**
     * 结果包裹器
     *
     * @param req       请求
     * @param resp      响应
     * @param timestamp 时间戳
     * @param code      异常代码
     * @param msg       信息
     * @param others    其他参数
     * @return
     */
    R process(Object req, Object resp, long timestamp, int code, String msg, Object... others);

}
