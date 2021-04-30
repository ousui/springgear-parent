package com.jd.springgear.exception;


import com.jd.springgear.support.constants.HttpStatus;

/**
 * 未授权异常，反馈给前端未 401 http status code
 *
 * @author "wangshuai131 <wangshuai30@jd.com>" 2017-12-12
 **/
public class GearUnauthorizedException extends SpringGearException {

    public GearUnauthorizedException(String msg) {
        super(msg, HttpStatus.SC_UNAUTHORIZED);
        this.setMonitored(false); // 不监控此类型
    }
}
