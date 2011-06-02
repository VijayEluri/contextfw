package net.contextfw.web.application.component;

import net.contextfw.web.application.internal.component.ComponentBuilder;

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
