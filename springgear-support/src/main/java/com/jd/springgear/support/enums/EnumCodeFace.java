package com.jd.springgear.support.enums;

/**
 * Enum 基本实现 code 的范型接口
 *
 * @param <T> 实现接口使用的 code 泛型
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2020/05/06
 **/
public interface EnumCodeFace<T> {

    /**
     * 返回 code
     *
     * @return T 范型
     */
    T getCode();

    /**
     * 使用范型 code 比较枚举是否一致
     *
     * @param code code
     * @return 比较结果
     */
    boolean same(T code);
}
