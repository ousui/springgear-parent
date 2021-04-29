package com.jd.springgear.exception;

import com.jd.springgear.support.constants.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2020/12/11
 **/
@AllArgsConstructor
public class SpringGearException extends Exception {

    @Getter
    private final int code;

    @Getter
    @Setter
    private long timestamp;

    @Getter
    @Setter
    private boolean monitored = true;

    public SpringGearException(String msg) {
        this(msg, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    public SpringGearException(String msg, int code) {
        this(msg, code, System.currentTimeMillis());
    }

    public SpringGearException(String msg, int code, long timestamp) {
        super(msg);
        this.code = code;
        this.timestamp = timestamp;
    }
}
