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

package net.contextfw.web.application;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A general exception thrown by application
 */ 
public class WebApplicationException extends RuntimeException {
    
    private static final long serialVersionUID = -3864752109086700032L;

    public WebApplicationException() {
        super();
    }

    public WebApplicationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public WebApplicationException(String msg) {
        super(msg);
    }

    public WebApplicationException(Throwable cause) {
        super(cause);
    }
    
    public WebApplicationException(Class<?> cl, String msg, Throwable cause) {
        super(cl.getName() +":" + msg, cause);
    }
    
    public WebApplicationException(Method method, String msg, Throwable cause) {
        super(method.getDeclaringClass().getName()+"."+method.getName()+"():" + msg, cause);
    }
    
    public WebApplicationException(Field field, String msg, Throwable cause) {
        super(field.getDeclaringClass().getName()+"."+field.getName()+":" + msg, cause);
    }
    
    public static final RuntimeException getRethrowable(Exception e) {
        if (e instanceof WebApplicationException) {
            return (WebApplicationException) e;
        } else {
            return new WebApplicationException(e);
        }
    }
    
    public static final RuntimeException getRethrowable(String msg, Exception e) {
        if (e instanceof WebApplicationException) {
            return (WebApplicationException) e;
        } else {
            return new WebApplicationException(msg, e);
        }
    }

}
