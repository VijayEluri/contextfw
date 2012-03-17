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

package net.contextfw.web.application.internal.component;

import net.contextfw.web.application.component.Component;

import com.google.inject.Inject;
import com.google.inject.spi.InjectionListener;

public class AutoRegisterListener<I extends Component> implements InjectionListener<I> {

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value="UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", 
            justification="Value is initialized by Guice")
    private ComponentBuilder componentBuilder;
    
    @Override
    public void afterInjection(I injectee) {
        componentBuilder.getMetaComponent(injectee.getClass()).registerChildren(injectee);
    }

    @Inject
    public void setComponentBuilder(ComponentBuilder componentBuilder) {
        this.componentBuilder = componentBuilder;
    }
}
