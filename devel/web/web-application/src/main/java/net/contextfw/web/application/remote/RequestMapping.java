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
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines a method in a <code>Component</code> that is called after building phase.
 * 
 * <p>
 *  When the DOM-representation of a component has been built, methods that has been annotated
 *  with this annotation are called. It enables component do cleaning up if necessary.
 * </p>
 * 
 * <p>
 *  The annotated method must not take any arguments. Possible return values are discarded.
 * </p>
 * 
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface RequestMapping {
    boolean includePath() default true;
}