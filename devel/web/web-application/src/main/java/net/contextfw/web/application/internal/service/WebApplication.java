package net.contextfw.web.application.internal.service;

import java.util.List;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;

import com.google.inject.ImplementedBy;

@ImplementedBy(WebApplicationImpl.class)
public interface WebApplication {

    /**
     * This is called when page is shown for the first time
     * 
     * @throws ContextServiceException
     */
    void initState() throws WebApplicationException;

    /**
     * 
     * @return
     *      true, if web application should be removed
     */
    boolean sendResponse();

    /**
     * This is called when page is updated
     * 
     * @throws ContextServiceException
     */
    UpdateInvocation updateState(boolean updateComponents, String componentId, String method) throws WebApplicationException;
    
    void setInitializerChain(List<Class<? extends Component>> chain);
}