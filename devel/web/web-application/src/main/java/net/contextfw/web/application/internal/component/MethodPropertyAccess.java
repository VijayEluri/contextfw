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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.internal.InternalWebApplicationException;

final class MethodPropertyAccess implements PropertyAccess<Object> {

    private static final class SetAccessibleAction implements PrivilegedAction<Void> {
        
        private final Method method;

        private SetAccessibleAction(Method method) {
            this.method = method;
        }

        @Override
        public Void run() {
            method.setAccessible(true);
            return null;
        }
    }

    private final Method method;
    
    public MethodPropertyAccess(final Method method) {
        if (method.getParameterTypes().length > 0) {
            throw new WebApplicationException("Method " + method.getDeclaringClass().getName() 
                    + "." + method.getName() + "() cannot take any parameters");
        }
        this.method = method;
        AccessController.doPrivileged(new SetAccessibleAction(method));
    }

    @Override
    public Object getValue(Object obj) {
         try {
            return method.invoke(obj);
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
        } catch (InvocationTargetException e) {
            if (WebApplicationException.class.isAssignableFrom(e.getCause().getClass())) {
                throw (RuntimeException) e.getCause();
            } else {
                throw new InternalWebApplicationException(e);
            }
        }
    }
}