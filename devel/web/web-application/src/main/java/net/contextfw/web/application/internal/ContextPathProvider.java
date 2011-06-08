package net.contextfw.web.application.internal;

import com.google.inject.Singleton;

@Singleton
public class ContextPathProvider {

	private String contextPath;

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getContextPath() {
		return contextPath;
	}
}
