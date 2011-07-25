package net.contextfw.web.application.internal.configuration;

import net.contextfw.web.application.configuration.TemporalProperty;

public class TemporalPropertyImpl implements TemporalProperty {

    private final String key;
    
    private final long value;
    
    public TemporalPropertyImpl(String key) {
        this.key = key;
        this.value = 0;
    }
    
    private TemporalPropertyImpl(String key, long value) {
        this.key = key;
        this.value = value;
    }
    
    @Override
    public String getKey() {
        return key;
    }

    public TemporalPropertyImpl inHoursAndMins(long hours, long mins) {
        return new TemporalPropertyImpl(key, (hours*60+mins)*60*1000);
    }
    
    public TemporalPropertyImpl inMinsAndSecs(long minutes, long seconds) {
        return new TemporalPropertyImpl(key, (seconds + minutes * 60) * 1000);
    }
    
    public TemporalPropertyImpl inSeconds(long seconds) {
        return inMillis(seconds * 1000);
    }
    
    public TemporalPropertyImpl inMinutes(long minutes) {
        return inSeconds(minutes * 60);
    }
    
    public TemporalPropertyImpl inMillis(final long millis) {
        return new TemporalPropertyImpl(key, millis);
    }

    public Long unserialize(String value) {
        return Long.parseLong(value);
    }

    @Override
    public String serialize(Long value) {
        return value.toString();
    }

    public Long validate(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("Property cannot be null");
        } if (value < 0) {
            throw new IllegalArgumentException("Property cannot be negative < 0");
        }
        return value;
    }

    @Override
    public Long getValue() {
        return value;
    }
}
