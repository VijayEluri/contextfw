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

package net.contextfw.web.application.internal.component;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.internal.InternalWebApplicationException;

final class FieldPropertyAccess<T> implements PropertyAccess<T> {

    private final Field field;
    
    public FieldPropertyAccess(final Field field) {
        this.field = field;
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                field.setAccessible(true);
                return null;
            }
        });
        
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getValue(Object obj) {
         try {
            return (T) field.get(obj);
        } catch (IllegalArgumentException e) {
            if (WebApplicationException.class.isAssignableFrom(e.getCause().getClass())) {
                throw (RuntimeException) e.getCause();
            } else {
                throw new InternalWebApplicationException(e);
            }
        } catch (IllegalAccessException e) {
            if (WebApplicationException.class.isAssignableFrom(e.getCause().getClass())) {
                throw (RuntimeException) e.getCause();
            } else {
                throw new InternalWebApplicationException(e);
            }
        }
    }
}