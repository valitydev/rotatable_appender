package com.rbkmoney.log.appender;

import ch.qos.logback.core.rolling.RollingFileAppender;

public class RotatableFileAppender<E> extends RollingFileAppender<E> {

    public long getCheckCachePeriod() {
        return getTriggeringPolicy().getCheckCachePeriod();
    }

    public void setCheckCachePeriod(long checkCachePeriod) {
        getTriggeringPolicy().setCheckCachePeriod(checkCachePeriod);
    }

    @Override
    public RotationBasedTriggeringPolicy<E> getTriggeringPolicy() {
        return (RotationBasedTriggeringPolicy<E>) super.getTriggeringPolicy();
    }

}
