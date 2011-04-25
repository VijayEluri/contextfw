package net.contextfw.web.application.remote;

import javax.servlet.http.HttpServletRequest;

import net.contextfw.web.application.component.Component;

public interface DelayedUpdateHandler<T extends Component> {
	
    boolean isUpdateDelayed(T component, HttpServletRequest request);
}
