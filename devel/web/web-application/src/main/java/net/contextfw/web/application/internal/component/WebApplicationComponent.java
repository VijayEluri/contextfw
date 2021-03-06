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
import net.contextfw.web.application.component.DOMBuilder;

import com.google.inject.Inject;

public class WebApplicationComponent extends Component {

    private final InternalComponentRegister elementRegister;

    private Component child = null;
    
    @Inject
    public WebApplicationComponent(InternalComponentRegister elementRegister) {
        this.elementRegister = elementRegister;
        elementRegister.register(this);
    }

    @Override
    protected boolean bubbleRegisterUp(Component el) {
        elementRegister.register(el);
        return true;
    }

    @Override
    protected void bubbleUnregisterUp(Component el) {
        elementRegister.unregister(el);
    }

    @Override
    public <T extends Component> T registerChild(T el) {
        this.child = super.registerChild(el);
        return el;
    }
    
    public void buildChild(DOMBuilder b) {
        b.child(child);
    }
    
    public void buildChildUpdate(DOMBuilder b, ComponentBuilder componentBuilder) {
        child.buildComponentUpdate(b, componentBuilder);
    }
}
