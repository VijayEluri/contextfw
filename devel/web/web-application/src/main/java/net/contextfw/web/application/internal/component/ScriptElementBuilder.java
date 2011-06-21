package net.contextfw.web.application.internal.component;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.DOMBuilder;
import net.contextfw.web.application.component.Script;
import net.contextfw.web.application.component.ScriptContext;

import com.google.gson.Gson;

class ScriptElementBuilder extends NamedBuilder {

    private final ScriptContext scriptContext;
    private final Gson gson;
    
    protected ScriptElementBuilder(ScriptContext scriptContext, Gson gson, PropertyAccess<Object> propertyAccess, String name, String accessName) {
        super(propertyAccess, name, accessName);
        this.scriptContext = scriptContext;
        this.gson = gson;
    }

    @Override
    void buildNamedValue(DOMBuilder b, String name, Object value) {
        if (value != null) {
        	if (value instanceof Script) {
        		((Script) value).build(b.descend(name), gson, scriptContext);
        	}
        	else {
        		throw new WebApplicationException("Instance of '"+value.getClass().getName()+"' is not a subclass of Script");
        	}
        }
    }
}
