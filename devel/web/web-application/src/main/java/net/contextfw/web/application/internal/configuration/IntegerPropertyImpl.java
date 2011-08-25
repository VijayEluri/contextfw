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

import net.contextfw.web.application.configuration.SettableProperty;

public class IntegerPropertyImpl extends BaseProperty<Integer> 
    implements SettableProperty<Integer> {

    public IntegerPropertyImpl(String key) {
        super(key);
    }

    @Override
    public Integer unserialize(String value) {
        return value == null ? null : Integer.parseInt(value);
    }

    @Override
    public String serialize(Integer value) {
        return value == null ? null : value.toString();
    }

    @Override
    public Integer validate(Integer value) {
        return value;
    }
}
