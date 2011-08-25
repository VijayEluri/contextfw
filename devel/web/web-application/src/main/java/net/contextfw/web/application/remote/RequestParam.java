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

package net.contextfw.web.application.remote;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * <p>
 *  Defines that the field or method contains encoded value from request parameters.
 * </p>
 * 
 * <h2>General usage</h2>
 * 
 * <p>
 *  This annotation can be applied to view component class properties or methods that take the expected
 *  type as singular parameter. When view components are initialized such methods and parameters
 *  are scanned and values are inserted. By default the class property name or method name is used
 *  to resolve parameter name. This can be overridden by using <code>name</code>.
 * </p>
 *  <p>
 *  At this points primitives and their wrappers are supported. Also any class having a 
 *  String-parametrized contructor is also supported.
 * </p>
 * <h3>Important caveat on initialization</h3>
 * 
 * <p>
 *  It is important to notice that parameters are not initialized during injection. That is 
 *  parameters are not initialized at @PostConstruct-time. To access them, view component
 *  needs to implement ViewComponent-interface thus initialization is ready when method
 *  initialize() is called. 
 * </p>
 * 
 * <h2>Exceptional handling</h2>
 * 
 * <p>
 *  Because path parameters are about URLs it is very likely that there will be malformed URLs
 *  and those cases must be handled somehow. To tackle those cases there are two handler
 *  <code>onError</code> and <code>onNull</code>.
 * 
 * @see ErrorResolution
 * 
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
public @interface RequestParam {
    /**
     * The name of the request parameter
     */
    String name() default "";
    /**
     * The resolution when mapping parameter to required type fails. 
     */
    ErrorResolution onError() default ErrorResolution.SEND_NOT_FOUND_ERROR;
    /**
     * The resolution when parameter is null
     */
    ErrorResolution onNull() default ErrorResolution.SET_TO_NULL;
}