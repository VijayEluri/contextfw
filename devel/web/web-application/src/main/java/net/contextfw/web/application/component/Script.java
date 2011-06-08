package net.contextfw.web.application.component;

import java.text.MessageFormat;

import org.apache.commons.lang.StringEscapeUtils;

import net.contextfw.web.application.internal.component.ComponentBuilder;

import com.google.gson.Gson;

/**
 * This class provides the base implementation for calling javascript commands on web client. 
 * 
 * <p>
 *  <code>Script</code> can be used as a class property or as a return value of a method. 
 *  The method or class property must be annotated with 
 *  {@link ScriptElement}-annotation to be recognized.
 * </p>
 * 
 * <p>
 *  The script is using {@link MessageFormat} as script format.
 */
public class Script {

	protected final String script;
	
	protected final Object[] params;

	/**
	 * Sole constructor
	 * 
	 * @param script
	 *    The script to be executed. Uses {@link MessageFormat} for injecting parameters
	 * @param params
	 *    The parameters to be injected in to the script. Parameters are run through Gson to 
	 *    format them into Javascript. 
	 *    
	 */
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
