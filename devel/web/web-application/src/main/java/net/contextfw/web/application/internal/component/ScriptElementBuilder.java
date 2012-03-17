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

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.DOMBuilder;
import net.contextfw.web.application.component.Script;
import net.contextfw.web.application.component.ScriptContext;

import com.google.gson.Gson;

class ScriptElementBuilder extends NamedBuilder {

    private final ScriptContext scriptContext;
    private final Gson gson;
    
    protected ScriptElementBuilder(
            ScriptContext scriptContext, 
            Gson gson, 
            PropertyAccess<Object> propertyAccess, 
            String name, 
            String accessName) {
        super(propertyAccess, name, accessName);
        this.scriptContext = scriptContext;
        this.gson = gson;
    }

    @Override
    void buildNamedValue(DOMBuilder b, String name, Object value) {
        if (value != null) {
        	if (value instanceof Script) {
        		((Script) value).build(b.descend(name), gson, scriptContext);
        	} else if (value instanceof Iterable) {
                for (Object i : ((Iterable<?>) value)) {
                    ((Script) i).build(b.descend(name), gson, scriptContext);
                }
        	} else if (value instanceof Object[]) {
        	    for (Object i : ((Object[]) value)) {
        	        ((Script) i).build(b.descend(name), gson, scriptContext);
        	    }
        	}  else {
        	    throw new WebApplicationException("Instance of '"+value.getClass().getName()+"' is not a subclass of Script");
        	}
        }
    }
}
