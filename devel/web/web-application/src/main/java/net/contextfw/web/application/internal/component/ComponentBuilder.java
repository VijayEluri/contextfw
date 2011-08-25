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

import java.util.Set;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.component.DOMBuilder;

import com.google.inject.ImplementedBy;

@ImplementedBy(ComponentBuilderImpl.class)
public interface ComponentBuilder {
    
    void build(DOMBuilder b, Object object, Object... buildIns);
    
    void buildUpdate(DOMBuilder b, Component component, String updateName);
    
    void buildPartialUpdate(DOMBuilder b, Component component, String updateName, Set<String> updates);
    
    boolean isBuildable(Class<?> cl);
    
    String getBuildName(Class<?> cl);
    
    MetaComponent getMetaComponent(Class<?> cl);
    
}