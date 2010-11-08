package net.contextfw.web.application;

public interface LifecycleListener {

    void beforeInitialize();
    
    void afterInitialize();
    
    void beforeUpdate();
    
    void afterUpdate();
    
    void onException(Exception e);
    
    void beforeRender();
    
    void afterRender();
}
