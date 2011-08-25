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

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * The default implementation for LifecycleListener
 */
@Singleton
public class DefaultLifecycleListener implements LifecycleListener {
    
    private Logger logger = LoggerFactory.getLogger(DefaultLifecycleListener.class);

    @Override
    public void beforeInitialize() {
    }

    @Override
    public void afterInitialize() {
    }

    @Override
    public boolean beforeUpdate() {
        return true;
    }

    @Override
    public void afterUpdate() {
    }

    @Override
    public void onException(Exception e) {
        logger.error("Caught exception", e);
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else {
            throw new WebApplicationException(e);
        }
    }

    @Override
    public void beforeRender() {
    }

    @Override
    public void afterRender() {
    }

    @Override
    public boolean beforeRemotedMethod(Component component, Method method, Object[] args) {
        return true;
    }

    @Override
    public void afterRemoteMethod(Component component, Method method, RuntimeException thrown) {
        if (thrown != null) { 
            throw thrown; 
        }
    }
}
