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

import net.contextfw.web.application.component.DOMBuilder;

class ElementBuilder extends NamedBuilder {

    private final ComponentBuilder componentBuilder;
    
    protected ElementBuilder(ComponentBuilder componentBuilder, PropertyAccess<Object> propertyAccess, String name, String accessName) {
        super(propertyAccess, name, accessName);
        this.componentBuilder = componentBuilder;
    }

    @Override
    void buildNamedValue(DOMBuilder b, String name, Object value) {
        if (value != null) {
            if (componentBuilder.isBuildable(value.getClass())) {
                componentBuilder.build(name == null ? b : b.descend(name), value);
            } else if (value instanceof Iterable) {
                DOMBuilder child = b.descend(name);
                for (Object i : ((Iterable<?>) value)) {
                    componentBuilder.build(child, i);
                }
            } else if (value instanceof Object[]) {
                DOMBuilder child = b.descend(name);
                for (Object i : ((Object[]) value)) {
                    componentBuilder.build(child, i);
                }
            } else {
                b.descend(name).text(value);
            }
        }
    }
}
