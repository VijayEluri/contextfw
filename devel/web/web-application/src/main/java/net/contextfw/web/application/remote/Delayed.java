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

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines if remoted method invocation should be delayed.
 * 
 * <p>
 *  It is possible to delay remote method invocation by annotating method with this
 *  annotation. When such method is about to be invoked the component is assigned to
 *  <code>DelayedUpdateHandler</code> which will determine if the call should be delayed. 
 * </p>
 * 
 * @see Remoted
 * @see DelayedUpdateHandler
 */
@Target( { METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Delayed {
    @SuppressWarnings("rawtypes")
	Class<? extends DelayedUpdateHandler> value();
}
