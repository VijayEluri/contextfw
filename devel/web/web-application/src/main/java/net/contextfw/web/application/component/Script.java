package net.contextfw.web.application.component;

import java.text.MessageFormat;

import org.apache.commons.lang.StringEscapeUtils;

import net.contextfw.web.application.internal.component.ComponentBuilder;

import com.google.gson.Gson;

public class Script {

	protected final String script;
	
	protected final Object[] params;

	public Script(String script, Object[] params) {
		this.script = script;
		this.params = params;
	}

	public Object[] getParams() {
		return params;
	}

	public void build(DOMBuilder b, Gson gson, ComponentBuilder componentBuilder) {
		MessageFormat format = new MessageFormat(getScript(componentBuilder));
		
		if (params == null) {
			b.text(getScript(componentBuilder));
		} else {
			b.text(format.format(getStringParams(gson, params)));
		}
	}
	
	private Object[] getStringParams(Gson gson, Object[] params) {
		Object[] rv = new Object[params.length];
		for (int i = 0; i < params.length; i++) {
			Object param = params[i];
	        if (Boolean.class.isAssignableFrom(param.getClass()) || Number.class.isAssignableFrom(param.getClass())) {
	            rv[i] = StringEscapeUtils.escapeJavaScript(param.toString());
	        } else {
	            rv[i] = gson.toJson(param);
	        }
	    }
		return rv;
	}
	
	protected String getScript(ComponentBuilder componentBuilder) {
		return script;
	}
}
