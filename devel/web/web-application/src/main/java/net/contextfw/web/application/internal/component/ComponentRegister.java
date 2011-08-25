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

import java.util.HashMap;
import java.util.Map;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.lifecycle.PageScoped;

@PageScoped
public class ComponentRegister {

    private int idCounter = 0;

    private Map<String, Component> components = new HashMap<String, Component>();

    private String getNextId() {
        return "el" + idCounter++;
    }

    public void register(Component component) {
        
        if (component.getId() == null) {
            component.setId(getNextId());
        }
        
        components.put(component.getId(), component);
    }

    public void unregister(Component component) {
        components.remove(component.getId());
    }

    public Component findComponent(String id) {
        return components.get(id);
    }
}