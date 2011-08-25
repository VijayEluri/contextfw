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

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.DOMBuilder;

class MethodCustomBuilder extends Builder {

    private final Method method;
    
    private final String name;
    
    public MethodCustomBuilder(Method method, String name) {
        super(method.getName());
        
        if (method.getParameterTypes().length == 0 
                || method.getParameterTypes().length > 1 
                || method.getParameterTypes()[0] != DOMBuilder.class) {
            throw new WebApplicationException("Method " + method.getDeclaringClass().getName() 
                    + "." + method.getName() + "() must take only one parameter of type DOMBuilder");
        }
        
        this.method = method;
        this.name = name;
    }
    
    @Override
    public void build(DOMBuilder b, Object buildable) {
        try {
            method.invoke(buildable, name == null ? b : b.descend(name));
        }
        catch (RuntimeException e) {
            throw new WebApplicationException(e);
        } catch (IllegalAccessException e) {
            throw new WebApplicationException(e);
        } catch (InvocationTargetException e) {
            throw new WebApplicationException(e);
        }
    }
}