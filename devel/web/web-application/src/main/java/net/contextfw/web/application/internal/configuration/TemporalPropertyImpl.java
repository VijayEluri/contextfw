/**
 * Copyright 2010 Marko Lavikainen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        return new TemporalPropertyImpl(key, (hours*60+mins) * 60 *1000); // NOSONAR
    }
    
    public TemporalPropertyImpl inMinsAndSecs(long minutes, long seconds) {
        return new TemporalPropertyImpl(key, (seconds + minutes * 60) * 1000); // NOSONAR
    }
    
    public TemporalPropertyImpl inSeconds(long seconds) {
        return inMillis(seconds * 1000); // NOSONAR
    }
    
    public TemporalPropertyImpl inMinutes(long minutes) {
        return inSeconds(minutes * 60); // NOSONAR
    }
    
    public TemporalPropertyImpl inMillis(final long millis) {
        return new TemporalPropertyImpl(key, millis);
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
