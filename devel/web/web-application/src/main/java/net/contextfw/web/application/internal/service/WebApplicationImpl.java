package net.contextfw.web.application.internal.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.PageFlowFilter;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.annotations.PageScoped;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.internal.ComponentUpdateHandler;
import net.contextfw.web.application.internal.ComponentUpdateHandlerFactory;
import net.contextfw.web.application.internal.WebResponder;
import net.contextfw.web.application.internal.WebResponder.Mode;
import net.contextfw.web.application.internal.component.ComponentBuilder;
import net.contextfw.web.application.internal.component.ComponentRegister;
import net.contextfw.web.application.internal.component.WebApplicationComponent;
import net.contextfw.web.application.internal.initializer.InitializerContextImpl;
import net.contextfw.web.application.internal.util.AttributeHandler;
import net.contextfw.web.application.properties.Properties;
import net.contextfw.web.application.request.Request;

import com.google.inject.Inject;
import com.google.inject.Injector;

@PageScoped
public class WebApplicationImpl implements WebApplication {

    @Inject
    private ComponentUpdateHandlerFactory euhf;

    @Inject
    private ComponentBuilder builder;

    private static volatile Map<String, ComponentUpdateHandler> updateHandlers = new HashMap<String, ComponentUpdateHandler>();

    @Inject
    private Injector injector;

    @Inject
    private HttpContext httpContext;

    @Inject
    private PageFlowFilter pageFlowFilter;

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
    public WebApplicationImpl(Properties props) {
        contextPath = props.get(Properties.CONTEXT_PATH);
        xmlParamName = props.get(Properties.XML_PARAM_NAME);
        debugMode = props.get(Properties.DEBUG_MODE);
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
            } catch (Exception e) {
                throw new WebApplicationException("Exception while trying to init state", e);
            }

        } catch (IOException e) {
            throw new WebApplicationException(e);
        }

        return false;
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
}