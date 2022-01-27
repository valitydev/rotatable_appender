package dev.vality.log.appender;

import ch.qos.logback.core.rolling.TriggeringPolicyBase;

import java.io.File;

public class RotationBasedTriggeringPolicy<E> extends TriggeringPolicyBase<E> {

    protected static class Clock {
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    }

    private final Clock clock;
    private long checkCachePeriod;
    private long nextTime;

    protected RotationBasedTriggeringPolicy(Clock clock) {
        this.clock = clock;
        this.checkCachePeriod = 1000;
    }

    public RotationBasedTriggeringPolicy() {
        this(new Clock());
    }

    public long getCheckCachePeriod() {
        return checkCachePeriod;
    }

    public void setCheckCachePeriod(long checkCachePeriod) {
        this.checkCachePeriod = checkCachePeriod;
    }

    @Override
    public boolean isTriggeringEvent(File activeFile, E event) {
        return !isCurrentTimeLessThanRotationTime();
    }

    private boolean isCurrentTimeLessThanRotationTime() {
        long now = clock.currentTimeMillis();
        if (now < nextTime) {
            return true;
        } else {
            nextTime = now + checkCachePeriod;
            return false;
        }
    }
}
