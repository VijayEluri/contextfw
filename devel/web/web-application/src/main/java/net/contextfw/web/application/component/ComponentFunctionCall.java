package net.contextfw.web.application.component;

import net.contextfw.web.application.internal.component.ComponentBuilder;

/**
 * Defines a javascript call invoked from server side.
 * 
 * <p>
 *  This class is a convinience class to make component based calls in with correct convention.
 * </p>
 * <p>
 *  The call will be in form: <code>ComponentName.methodName([args,...]);
 * </p>
 */
public class ComponentFunctionCall extends FunctionCall {
	
	private final Class<? extends Component> clazz;
	private final String id;
	
	/**
	 * Sole constructor 
	 * 
	 * @param component
	 *   The component where this is to be invoked
	 * @param function
	 *   The function name
	 * @param args
	 *   The arguments.
	 */
	public ComponentFunctionCall(Component component, String function, Object... args) {
		super(function, args);
		this.clazz = component.getClass();
		this.id = component.getId();
	}
	
	@Override
	protected String getScript(ComponentBuilder componentBuilder) {
		return FunctionCall.toScript(componentBuilder.getBuildName(clazz) 
				+ "(\"" + id +"\")." 
				+ script, 
				getParams() == null ? 0 : getParams().length);
	}
}
