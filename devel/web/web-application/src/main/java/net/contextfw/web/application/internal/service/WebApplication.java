package net.contextfw.web.application.internal.service;

import java.util.List;

import net.contextfw.web.application.component.Component;

import com.google.inject.ImplementedBy;

@ImplementedBy(WebApplicationImpl.class)
public interface WebApplication {

    /**
     * This is called when page is shown for the first time
     */
    void initState();

    /**
     * 
     * @return
     *      true, if web application should be removed
     */
    boolean sendResponse();

    /**
     * This is called when page is updated
     */
    UpdateInvocation updateState(boolean updateComponents, String componentId, String method);
    
    void setInitializerChain(List<Class<? extends Component>> chain);
}