package net.contextfw.web.application.internal.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.component.DOMBuilder;
import net.contextfw.web.application.internal.ComponentUpdateHandler;
import net.contextfw.web.application.internal.ComponentUpdateHandlerFactory;
import net.contextfw.web.application.internal.ContextPathProvider;
import net.contextfw.web.application.internal.WebResponder;
import net.contextfw.web.application.internal.WebResponder.Mode;
import net.contextfw.web.application.internal.component.ComponentBuilder;
import net.contextfw.web.application.internal.component.ComponentRegister;
import net.contextfw.web.application.internal.component.WebApplicationComponent;
import net.contextfw.web.application.internal.initializer.InitializerContextImpl;
import net.contextfw.web.application.internal.util.AttributeHandler;
import net.contextfw.web.application.lifecycle.PageScoped;
import net.contextfw.web.application.lifecycle.ResourceView;
import net.contextfw.web.application.properties.Properties;
import net.contextfw.web.application.remote.ResourceResponse;
import net.contextfw.web.application.util.Request;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Injector;

@PageScoped
public class WebApplicationImpl implements WebApplication {

    @Inject
    private Gson gson;
    
    @Inject
    private ComponentUpdateHandlerFactory euhf;

    @Inject
    private ComponentBuilder builder;

    private static volatile Map<String, ComponentUpdateHandler> updateHandlers = new HashMap<String, ComponentUpdateHandler>();

    @Inject
    private Injector injector;

    @Inject
    private HttpContext httpContext;

//    @Inject
//    private PageFlowFilter pageFlowFilter;

    private final ComponentRegister componentRegister = new ComponentRegister();

    private final WebApplicationComponent rootComponent =
            new WebApplicationComponent(componentRegister);

    private List<Class<? extends Component>> chain;

    private InitializerContextImpl context;

    @Inject
    private WebResponder responder;

    private Mode mode = Mode.INIT;

    @Inject
    private AttributeHandler attributes;

    @Inject
    private WebApplicationHandle webApplicationHandle;

    private final String contextPath;
    
    private final String xmlParamName;
    
    private final boolean debugMode;
    
    @Inject
    public WebApplicationImpl(Properties props, ContextPathProvider contextPathProvider) {
        contextPath = contextPathProvider.getContextPath();
        xmlParamName = props.get(Properties.XML_PARAM_NAME);
        debugMode = props.get(Properties.DEVELOPMENT_MODE);
    }

    @Override
    public void initState() throws WebApplicationException {
        context = new InitializerContextImpl(injector, chain);
        rootComponent.registerChild(context.initChild());
    }

    @Override
    public boolean sendResponse() {

        try {
            if (mode == Mode.INIT) {
                if (httpContext.getRedirectUrl() != null) {
                    httpContext.getResponse().sendRedirect(httpContext.getRedirectUrl());
                    return true;
                } else if (httpContext.getErrorCode() != null) {
                    httpContext.getResponse().sendError(httpContext.getErrorCode(), httpContext.getErrorMsg());
                    return true;
                } else if (httpContext.isReload()) {
                    httpContext.getResponse().sendRedirect(httpContext.getFullUrl());
                }
            }

            try {
                if (context.getLeaf() instanceof ResourceView) {
                    sendResourceResponse();
                    return true;
                } else {
                    sendNormalResponse();
                    return false;
                }
            } catch (Exception e) {
                throw new WebApplicationException("Exception while trying to init state", e);
            }

        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
    }

    private void sendResourceResponse() throws IOException {
        Object retVal = ((ResourceView) context.getLeaf()).getResponse();
        if (retVal == null) {
            return;
        }
        if (retVal instanceof ResourceResponse) {
            ((ResourceResponse) retVal).serve(
                    httpContext.getRequest(), 
                    httpContext.getResponse());
        } else {
            HttpServletResponse response = httpContext.getResponse();
            setHeaders(response);
            response.setContentType("application/json; charset=UTF-8");
            gson.toJson(retVal, response.getWriter());
        }
        
    }

    private void sendNormalResponse() throws ServletException, IOException {
        httpContext.getResponse().setContentType("text/html; charset=UTF-8");

        DOMBuilder d;

        if (mode == Mode.INIT) {
            d = new DOMBuilder("WebApplication", attributes, builder);
        } else {
            d = new DOMBuilder("WebApplication.update", attributes, builder);
        }

        d.attr("handle", webApplicationHandle.getKey());
        d.attr("contextPath", contextPath);

        if (context.getLocale() != null) {
            d.attr("xml:lang", context.getLocale().toString());
            d.attr("lang", context.getLocale().toString());
        }
        if (mode == Mode.INIT) {
            rootComponent.buildChild(d);
        } else if (httpContext.getRedirectUrl() != null) {
            d.descend("Redirect").attr("href", httpContext.getRedirectUrl());
        } else if (httpContext.getErrorCode() != null) {
            d.descend("Error").attr("code", httpContext.getErrorCode()).text(httpContext.getErrorMsg());
        } else if (httpContext.isReload()) {
            d.descend("Reload");
        } else {
            rootComponent.buildChildUpdate(d, builder);
        }

        rootComponent.clearCascadedUpdate();

        if (xmlParamName == null
                || httpContext.getRequest().getParameter(xmlParamName) == null) {
            responder.sendResponse(d.toDocument(), httpContext.getResponse(), mode);
        } else {
            responder.sendResponse(d.toDocument(), httpContext.getResponse(), Mode.XML);
        }
    }

    @Override
    public UpdateInvocation updateState(boolean updateComponents, String componentId, String method) throws WebApplicationException {
        mode = Mode.UPDATE;
        if (updateComponents) {
            return updateElements(componentId, method);
        } else {
            return UpdateInvocation.NOT_DELAYED;
        }
    }

    @SuppressWarnings("unchecked")
    protected UpdateInvocation updateElements(final String id, final String method) throws WebApplicationException {
        try {
            Request request = new Request(httpContext.getRequest());
            Component element = componentRegister.findComponent(id);
            String key = ComponentUpdateHandler.getKey(element.getClass(), method);

                if (!updateHandlers.containsKey(key) || debugMode) {
                    updateHandlers.put(key, euhf.createHandler(element.getClass(), method));
                }

                ComponentUpdateHandler handler = updateHandlers.get(key);

                if (handler != null) {
                    if (handler.getDelayed() == null
                                || !injector.getInstance(handler.getDelayed().value())
                                  .isUpdateDelayed(element, httpContext.getRequest())) {
                        return new UpdateInvocation(
                                handler.isResource(),
                                handler.invoke(element, request)
                                );
                    } else {
                        return UpdateInvocation.DELAYED;
                    }
                } else {
                    return UpdateInvocation.NOT_DELAYED;
                }
        } catch (Exception e) {
            throw new WebApplicationException("Failed to update elements", e);
        }
    }

    @Override
    public void setInitializerChain(List<Class<? extends Component>> chain) {
        this.chain = chain;
    }
    
    public void setHeaders(HttpServletResponse response) {
        response.addHeader("Expires", "Sun, 19 Nov 1978 05:00:00 GMT");
        response.addHeader("Last-Modified", new Date().toString());
        response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // response.addHeader("Cache-Control","post-check=0, pre-check=0");
        response.addHeader("Pragma", "no-cache");
        response.setHeader("Connection", "Keep-Alive");
    }
}