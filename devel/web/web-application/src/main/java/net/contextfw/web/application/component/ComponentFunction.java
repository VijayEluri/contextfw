package net.contextfw.web.application.component;

import net.contextfw.web.application.internal.component.ComponentBuilder;

public class ComponentFunction extends Function {
	
	private final Class<? extends Component> clazz;
	private final String id;
	
	public ComponentFunction(Component component, String function, Object... args) {
		super(function, args);
		this.clazz = component.getClass();
		this.id = component.getId();
	}
	
	@Override
	protected String getScript(ComponentBuilder componentBuilder) {
		return Function.toScript(componentBuilder.getBuildName(clazz) 
				+ "(\"" + id +"\")." 
				+ script, 
				getParams() == null ? 0 : getParams().length);
	}
}
