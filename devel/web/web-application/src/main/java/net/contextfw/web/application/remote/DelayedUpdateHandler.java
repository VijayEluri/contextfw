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

import javax.servlet.http.HttpServletRequest;

/**
 * The base interface for handling delayed updates
 * 
 * <p>
 *  When an update handler is called it must make appropriate preparations to delay invocation.
 *  It should be noted that the framework itself does note provide any delaying features because
 *  they are web container specific. That is for instance, Jetty works differently from Tomcat.
 * </p>
 * <p>
 *  It is developers responsibility to use Continuations or similar features to create proper
 *  delay.
 * </p>
 * 
 * @param <T>
 *   The type of component
 */
public interface DelayedUpdateHandler<T> {
	
    /**
     * Returns <code>true</code> if update should be delayed
     * @param component
     *   The component
     * @param request
     *   The request that is used to create the delay
     * @return
     *   <code>true</code> if update should be delayed, false otherwise
     */
    boolean isUpdateDelayed(T component, HttpServletRequest request);
}
