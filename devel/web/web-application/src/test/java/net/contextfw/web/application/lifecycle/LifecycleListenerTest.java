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

import static org.junit.Assert.assertTrue;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.lifecycle.DefaultLifecycleListener;
import net.contextfw.web.application.lifecycle.LifecycleListener;

import org.junit.Test;

public class LifecycleListenerTest {

    private LifecycleListener listener = new DefaultLifecycleListener();
    
    @Test
    public void Basic_Test() {
        assertTrue(listener.beforeInitialize(null, null, null));
        assertTrue(listener.beforeUpdate(null, null, null));
        assertTrue(listener.beforeRemotedMethod(null, null, null));
        // Tests that with null exception no exception is thrown
        listener.afterRemoteMethod(null, null, null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void After_Remote_Method_Trows_Exception() {
        listener.afterRemoteMethod(null, null, new IllegalArgumentException());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void On_Exception_Throws_Runtime_Exception() {
        listener.onException(new IllegalArgumentException());
    }
    
    @Test(expected=WebApplicationException.class)
    public void On_Exception_Throws_Web_Application_Exception() {
        listener.onException(new NoSuchMethodException());
    }
    
}
