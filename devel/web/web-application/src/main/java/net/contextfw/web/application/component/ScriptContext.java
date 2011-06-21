package net.contextfw.web.application.component;

public interface ScriptContext {

    boolean isBuildable(Class<?> cl);
    
    String getBuildName(Class<?> cl);
}
