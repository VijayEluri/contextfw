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

package net.contextfw.web.application.internal;

import java.lang.reflect.Method;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.internal.util.ClassScanner;
import net.contextfw.web.application.lifecycle.LifecycleListener;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ComponentUpdateHandlerFactory {
    
    private Gson gson;
    
    private final LifecycleListener listener;
    
    @Inject
    public ComponentUpdateHandlerFactory(Gson gson, LifecycleListener listener) {
        this.gson = gson;
        this.listener = listener;
    }
    
    public ComponentUpdateHandler createHandler(Class<? extends Component> elClass, String methodName) {

        Method method = ClassScanner.findMethodForName(elClass, methodName);

        if (method != null) {
            return new ComponentUpdateHandler(
                    ComponentUpdateHandler.getKey(elClass, methodName), method, gson, listener);
        }
        else {
            return null;
        }
    }
}