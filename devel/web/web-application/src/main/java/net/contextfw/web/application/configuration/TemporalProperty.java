package net.contextfw.web.application.configuration;

import net.contextfw.web.application.internal.configuration.SelfSettableProperty;

public interface TemporalProperty extends SelfSettableProperty<Long> {
    
    TemporalProperty inHoursAndMins(long hours, long mins);
    
    TemporalProperty inMinsAndSecs(long minutes, long seconds);
    
    TemporalProperty inSeconds(long seconds);
    
    TemporalProperty inMinutes(long minutes);
    
    TemporalProperty inMillis(final long millis);
}
