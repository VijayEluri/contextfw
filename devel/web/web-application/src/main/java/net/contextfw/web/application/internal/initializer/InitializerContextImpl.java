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

package net.contextfw.web.application.internal.initializer;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.internal.component.ComponentBuilder;
import net.contextfw.web.application.internal.component.MetaComponent;
import net.contextfw.web.application.internal.servlet.UriMapping;
import net.contextfw.web.application.lifecycle.ViewComponent;
import net.contextfw.web.application.lifecycle.ViewContext;

import com.google.inject.Injector;

public class InitializerContextImpl implements ViewContext {

    private final transient List<Class<? extends Component>> chain;
    private final transient Injector injector;
    
    private int currentIndex = 0;
    
    private Locale locale = null;
    
    private transient Component leaf;
    
    private final transient ComponentBuilder componentBuilder;
    private final transient UriMapping mapping;
    private final transient String uri;
    private final transient HttpServletRequest request;
    
    public InitializerContextImpl(ComponentBuilder componentBuilder,
                                  UriMapping mapping,
                                  String uri,
                                  Injector injector,
                                  HttpServletRequest request,
                                  List<Class<? extends Component>> chain) {
        this.chain = chain;
        this.injector = injector;
        this.componentBuilder = componentBuilder;
        this.mapping = mapping;
        this.uri = uri;
        this.request = request;
    }
    
    @Override
    public Class<? extends Component> getChildClass() {
        if (currentIndex == chain.size()) {
            return null;
        } else {
            return chain.get(currentIndex);
        }
    }

    @Override
    public Component initChild() {
        
        Class<? extends Component> cl = getChildClass();
        
        if (cl == null) {
            throw new WebApplicationException("Error getting a child initializer. Initializer " 
                    + chain.get(currentIndex-1).getName() + " does not have any children");
        }
        Component component = injector.getInstance(cl);
        MetaComponent meta = componentBuilder.getMetaComponent(cl);
        meta.applyPathParams(component, mapping, uri);
        meta.applyRequestParams(component, request);
        
        leaf = component;
        
        if (ViewComponent.class.isAssignableFrom(cl)) {
            currentIndex++;
            ((ViewComponent) component).initialize(this);
        }
        
        return component;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    public Locale getLocale() {
        return locale;
    }

    public Component getLeaf() {
        return leaf;
    }
}