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

import java.lang.reflect.Method;

import net.contextfw.web.application.component.Component;

/**
 * This interface defines means to follow page lifecyle.
 * 
 * <p>
 *  This listener is meant to be used to follow and to react page lifecycle. Using lifecycle listener 
 *  is especially handy when possible database connections needs
 *  to be opened and closed during the lifecycle of a request.
 * </p>
 * 
 * <p>
 *  Can be set by <code>LIFECYCLE_LISTENER</code> by Configuration
 * </p>
 * 
 * <p>
 *  There is also another class <code>PageFlowFilter</code> that serves 
 *  similar purpose but is meant for gathering statistics and throttling
 *  requests if needed.
 * </p>
 * 
 * @see net.contextfw.web.application.configuration.Configuration.LIFECYCLE_LISTENER
 * @see PageFlowFilter
 */
public interface LifecycleListener {

    /**
     * Called by framework before page initialization begins
     */
    void beforeInitialize();
    
    /**
     * Called by framework after page initialization has ended
     */
    void afterInitialize();
    
    /**
     * <p>
     *  Called by framework before update
     * </p>
     * <p>
     *  This method returns a boolean. When <code>true</code> update can continue and requests
     *  from web client are processed. If <code>false</code> request processing is bypassed.
     * </p>
     * <p>
     *  Returning <code>false</code> false is useful in cases where request cannot be
     *  accepted for instance if user credentials have expired. This is needed because
     *  update requests cannot easily be prevented with url-based filtering techniques.
     * </p>
     * <p>
     *  In case of <code>false</code> (and also when <code>true</code>) this method can be used to
     *  call methods on components and create updates. This is useful if page should be redirected
     *  or some message component should display something on web page.
     * </p>
     * @return <code>true</code> if client update can continue, <code>false</code> otherwise
     */
    boolean beforeUpdate();
    
    /**
     * <p>
     *  This method is called just before remote method is to be invoked
     * </p>
     * 
     * <p>
     *  When remote method is to be invoked it is run through this handler. It's 
     *  main purpose is for data validation or mngling. The arguments <code>args</code>
     *  is modifiable and changes reflected to it, are also reflected to actual call.
     * </p>
     * 
     * <p>
     *  This method works basically as a proxy between requests and actual method calls.
     *  When using this method, you do not need to create Guice proxies for components.
     * </p>
     * 
     * @param component
     *   The component where the call is made
     * @param method
     *   The method to be invoked
     * @param args
     *   The method arguments. Empty array if no arguments.
     * @return
     *   <code>true</code> if method is to be be invoked. <code>false</code> prevents 
     *   method invocation.
     */
    boolean beforeRemotedMethod(Component component, Method method, Object[] args);

    /**
     * Invoked after the remote method invocation has finished.
     * 
     * <p>
     *  If method throws an exception it is given as argument. This allows system to 
     *  react exceptional condition. 
     * </p>
     * <p>
     *  Also, if parameter parsing from client side fails, the exception is returned also. 
     *  The method can re throw the exception or throw a new one. When throwing an exception
     *  it should be remembered that it is caught by method onException().
     * </p>
     * <p>
     *  If remote method was not called due beforeRemoteMethod() returning false 
     *  then this method is also bypassed.
     * </p>
     * @param component
     *   The component where the call is made
     * @param method
     *   The method invoked
     * @param thrown
     *   The exception if it was thrown
     */
    void afterRemoteMethod(Component component, Method method, RuntimeException thrown);

    /**
     * Called by framework after update has finished
     */
    void afterUpdate();
    
    /**
     * Called by framework if any exception is thrown.
     * 
     * <p>
     *  After this call pageflow is compeletely cancelled and no further calls are
     *  made, so it is safe for instance to close database connection here.
     * </p>
     */
    void onException(Exception e);
    
    /**
     * Called before rendering phase
     */
    void beforeRender();
    
    /**
     * Called after rendering phase
     */
    void afterRender();
}
