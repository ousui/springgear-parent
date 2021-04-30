package com.jd.springgear.extend.ducc;

/**
 * ducc logger 动态变更器
 *
 * @author wangshuai131 (<a href="mailto:wangshuai30@jd.com">wangshuai30@jd.com</a>)
 * @since 2020/12/19
 **/
public enum DuccLogger {

    LOG4J2,
    LOG4J,
    LOGBACK;

    public LoggerDuccConfigurator getLoggerDuccConfigurator() {
        return new LoggerDuccConfigurator("logger.level", this);
    }

    public LoggerDuccConfigurator getLoggerDuccConfigurator(String key) {
        return new LoggerDuccConfigurator(key, this);
    }

    private class LoggerDuccConfigurator extends JavaScriptDuccConfigurator {

        private static final String TPL = "script/%s.js";

        private final DuccLogger type;

        public LoggerDuccConfigurator(String key, DuccLogger type) {
            super(key);
            this.type = type;
        }

        @Override
        protected String getScriptPath() {
            return String.format(TPL, type.toString().toLowerCase());
        }
    }


}
