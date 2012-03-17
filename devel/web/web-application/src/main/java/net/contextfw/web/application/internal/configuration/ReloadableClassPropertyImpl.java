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

import java.util.HashSet;
import java.util.Set;

import net.contextfw.web.application.configuration.ReloadableClassProperty;

import org.apache.commons.lang.StringUtils;

public class ReloadableClassPropertyImpl extends SelfSetPropertyImpl<Object> implements ReloadableClassProperty {

    public ReloadableClassPropertyImpl(String key) {
        super(key);
    }
    
    private ReloadableClassPropertyImpl(String key, Object value) {
        super(key, value);
    }

    @Override
    public ReloadableClassProperty includedPackage(String name) {
        return includedPackage(name, true);
    }

    @Override
    public ReloadableClassProperty includedPackage(String name, boolean recursive) {
        String trimmedName = StringUtils.trimToNull(name);
        if (trimmedName== null) {
            throw new IllegalArgumentException("Package name cannot be empty");
        }
        return new ReloadableClassPropertyImpl(getKey(), trimmedName + ":" + recursive);
    }

    @Override
    public ReloadableClassProperty excludedClass(Class<?>... clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Classes cannot be null");
        }
        Set<Class<?>> classes = new HashSet<Class<?>>();
        for (Class<?> cl : clazz) {
            classes.add(cl);
        }
        return new ReloadableClassPropertyImpl(getKey(), classes); 
    }

}
 