package com.jd.springgear.extend.jsf.engine;

import com.google.common.collect.Lists;
import com.jd.jsf.gd.util.RpcContext;
import com.jd.springgear.engine.AbstractSpringGearEngineExecutor;
import com.jd.springgear.exception.GearInterruptException;
import com.jd.springgear.support.constants.HttpStatus;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2020/12/13
 **/
@Slf4j
public class JsfSpringGearEngineExecutor extends AbstractSpringGearEngineExecutor {

    private final static String JSF_PARAMETER_SOURCE = "source";

    private final static String DEFAULT_SOURCE = "NON_JSF";
    private final static String UNKNOWN_SOURCE = "JSF_UNKNOWN";

    /**
     * 杰夫来源参数
     */
    @Setter
    private String jsfParameterSource;


    /**
     * 来源是否验证
     */
    @Setter
    private boolean verifySource = false;

    /**
     * 如果验证，允许的 source，default source 默认是被允许的
     */
    @Setter
    private List<String> allowSources;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (jsfParameterSource == null) {
            jsfParameterSource = JSF_PARAMETER_SOURCE;
        }
        if (allowSources == null) {
            allowSources = Lists.newArrayList();
        }
    }

    @Override
    protected String getSource() throws GearInterruptException {

        RpcContext rpcContext = RpcContext.getContext();

        // 标记是否为杰夫调用。
        boolean isJsfInvoke = (rpcContext.isProviderSide() && rpcContext.getAlias() != null);


        if (!isJsfInvoke) { // 非杰夫来源，认为是 http 请求
            return DEFAULT_SOURCE;
        }

        // 从参数获取来源
        String source = (String) rpcContext.getAttachment(this.jsfParameterSource);

        // 如果为空
        if (source == null) {
            if (this.verifySource) { // 如果需要验证，报错
                throw new GearInterruptException("请为 <jsf:consumer> 添加名为 'source' 的 parameter。", HttpStatus.SC_BAD_REQUEST);

            } else { // 如果不需要验证，赋默认值
                source = UNKNOWN_SOURCE;
            }
        }

        log.info("SOURCE[{}] REMOTE[{}] ALIAS[{}]", source, rpcContext.getRemoteHostName(), rpcContext.getAlias());

        // 如果无需验证，返回
        if (false == this.verifySource) {
            return source;
        }

        // 需要验证，则处理允许列表
        // 此处可改为动态配置形式
        if (false == allowSources.contains(source)) {
            throw new GearInterruptException(String.format("source '%s' 不在允许使用的范围内。", source), HttpStatus.SC_FORBIDDEN);
        }

        return source;
    }
}
