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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.component.DOMBuilder;
import net.contextfw.web.application.component.ScriptContext;
import net.contextfw.web.application.internal.util.AttributeHandler;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ComponentBuilderImpl implements ComponentBuilder, ScriptContext {

    private static final Map<Class<?>, MetaComponent> metaModels = 
        new HashMap<Class<?>, MetaComponent>();
    private static final Map<Class<?>, Class<?>> actualClasses =
        new WeakHashMap<Class<?>, Class<?>>();

    private final AttributeHandler attributeHandler;

    private final Gson gson;

    @Inject
    public ComponentBuilderImpl(AttributeHandler attributeHandler, Gson gson) {
        this.attributeHandler = attributeHandler;
        this.gson = gson;
    }

    @Override
    public MetaComponent getMetaComponent(final Class<?> cl) {
        Class<?> actual = actualClasses.get(cl);
        if (actual == null) {
            actual = getActualClass(cl);
            actualClasses.put(cl, actual);
        }
        MetaComponent model = metaModels.get(actual);
        if (model == null) {
            model = new MetaComponent(actual, this, gson, this);
            metaModels.put(actual, model);
        }
        return model;
    }
    
    public void clean() {
        metaModels.clear();
        actualClasses.clear();
    }

    public static Class<?> getActualClass(Object element) {
        return getActualClass(element.getClass());
    }

    public static Class<?> getActualClass(Class<?> cl) {
        Class<?> actual = actualClasses.get(cl);
        if (actual != null) {
            return actual;
        } else {
            actual = cl;
            while (actual.getSimpleName().contains("EnhancerByGuice")) {
                actual = actual.getSuperclass();
            }
            actualClasses.put(cl, actual);
            return actual;
        }
    }

    @Override
    public String getBuildName(Class<?> cl) {
        MetaComponent model = getMetaComponent(cl);
        if (model.annotation != null) {
            return model.buildName;
        } else {
            throw new WebApplicationException(cl, "Class is not buildable", null);
        }
    }

    @Override
    public void build(DOMBuilder sb, Object component, Object... buildins) {
        MetaComponent model = getMetaComponent(component.getClass());
        if (model.annotation != null) {
            if (component instanceof Component && !((Component) component).isEnabled()) {
                return;
            }
            DOMBuilder b = model.buildName == null ? sb : sb
                    .descend(model.buildName);
            build(model, b, component, model.builders, false, null, buildins);
        } else {
            sb.text(attributeHandler.serialize(component));
        }
    }

    private void build(MetaComponent model, DOMBuilder b, Object component,
            List<Builder> builders, boolean partial, Set<String> updates,
            Object... buildins) {
        
        model.applyBeforeBuilds(component);

        if (partial) {
            for (Builder builder : builders) {
                if (builder.isUpdateBuildable(updates)) {
                    builder.build(b, component);
                }
            }
        } else {
            for (Builder builder : builders) {
                builder.build(b, component);
            }
        }

        // Handling buildins

        if (buildins != null) {
            for (Object buildIn : buildins) {
                if (buildIn != null) {
                    MetaComponent bmodel = getMetaComponent(buildIn.getClass());
                    if (bmodel.annotation != null) {
                        for (Builder builder : bmodel.builders) {
                            builder.build(b, buildIn);
                        }
                    }
                }
            }
        }

        model.applyAfterBuilds(component);
        
    }

    @Override
    public void buildUpdate(DOMBuilder sb, Component component,
            String updateName) {
        MetaComponent model = getMetaComponent(component.getClass());
        if (model.annotation != null) {
            DOMBuilder b = sb.descend(model.buildName + "." + updateName);
            build(model, b, component, model.updateBuilders, false, null,
                    (Object[]) null);
        }
    }

    @Override
    public void buildPartialUpdate(DOMBuilder sb, Component component,
            String updateName, Set<String> updates) {
        MetaComponent model = getMetaComponent(component.getClass());
        if (model.annotation != null) {
            DOMBuilder b = sb.descend(model.buildName + "." + updateName);
            build(model, b, component, model.partialBuilders, true, updates,
                    (Object[]) null);
        }
    }

    @Override
    public boolean isBuildable(Class<?> cl) {
        return getMetaComponent(cl).annotation != null;
    }
}