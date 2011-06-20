package net.contextfw.web.application.configuration;

public class TemporalProperty implements SelfSettableProperty<Long> {

    private final String key;
    
    private final long value;
    
    public TemporalProperty(String key) {
        this.key = key;
        this.value = 0;
    }
    
    private TemporalProperty(String key, long value) {
        this.key = key;
        this.value = value;
    }
    
    @Override
    public String getKey() {
        return key;
    }

    public TemporalProperty inHoursAndMins(long hours, long mins) {
        return new TemporalProperty(key, (hours*60+mins)*60*1000);
    }
    
    public TemporalProperty inMinsAndSecs(long minutes, long seconds) {
        return new TemporalProperty(key, (seconds + minutes * 60) * 1000);
    }
    
    public TemporalProperty inSeconds(long seconds) {
        return inMillis(seconds * 1000);
    }
    
    public TemporalProperty inMinutes(long minutes) {
        return inSeconds(minutes * 60);
    }
    
    public TemporalProperty inMillis(final long millis) {
        return new TemporalProperty(key, millis);
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
