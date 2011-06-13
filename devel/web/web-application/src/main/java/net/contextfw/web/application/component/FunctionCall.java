package net.contextfw.web.application.component;

import net.contextfw.web.application.internal.component.ComponentBuilder;

/**
 * Defines a javascript call invoked from server side.
 * 
 * <p>
 *  This class is a convinience class to make calls in with correct convention.
 * </p>
 * <p>
 *  The call will be in form: <code>functionName([args,...]);
 * </p>
 */
public class FunctionCall extends Script {
	
	public FunctionCall(String function, Object... args) {
		super(function, args);
	}
	
	protected static String toScript(String function, int argCount) {
		StringBuilder b = new StringBuilder(function).append("(");
		String delim = "";
		for (int i = 0; i < argCount; i++) {
			b.append(delim).append("{").append(i).append("}");
			delim = ",";
		}
		b.append(");\n");
		return b.toString();
	}
	
	@Override
	protected String getScript(ComponentBuilder componentBuilder) {
		return FunctionCall.toScript(script, 
				getParams() == null ? 0 : getParams().length);
	}
}
