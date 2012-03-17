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

package net.contextfw.web.application.component;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Denotes that a class property or return value of a method is to be added as element attribute 
 * into DOM-tree during build phase.
 * 
 * <p>
 *  If property or return value is <code>null</code> then attribute is not added to the tree.
 * </p>
 */
@Target( { FIELD, METHOD })
@Retention(RUNTIME)
public @interface Attribute {
    
    /**
     * The name of the attribute.
     * 
     * <p>
     *  Defaults to the property or method name
     * </p>
     */
    String name() default "";
    
    /**
     * Defines if this attribute should be built in to DOM-tree during component creation  
     */
    boolean onCreate() default true;
    
    /**
     * Defines if this attribute should be built in to DOM-tree during component update  
     */
    boolean onUpdate() default true;
    
}