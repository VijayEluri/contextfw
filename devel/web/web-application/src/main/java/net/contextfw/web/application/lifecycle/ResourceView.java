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

package net.contextfw.web.application.lifecycle;

/**
 * <p>
 *  Defines that page view should respond with resource.
 * </p>
 * 
 * <p>
 *  If a view component is implementing this interface, it is considered
 *  being returning resources rather than normal web page. Resource can  
 *  be anything from JSON to plain text-files or images.
 * </p>
 * 
 * <p>
 *  Normally page scope regarding to this view is immediately expired, becuse
 *  it has no purpose. However, using ResourceBody-annotation the expiration can
 *  be disabled. Is needed if system is used in embedded mode.
 * </p>
 * 
 * @see net.contextfw.web.application.remote.ResourceBody
 */
public interface ResourceView {

    /**
     * Send a response to the client.
     * 
     * <p>
     *  This method must return the response that is sent to web client. 
     *  There are two possibilities. If return values is a subclass of
     *  {@link net.contextfw.web.application.remote.ResourceResponse}
     *  the actual response is served from it.
     * </p>
     * <p>
     *  Otherwise return value is considered to be JSON and is automatically processed
     *  and sent.
     * </p>
     * @return
     *    JSON or <code>ResourceResponse</code>
     */
    Object getResponse();
}
