package net.contextfw.web.application.internal.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.ModuleConfiguration;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.annotations.WebApplicationScoped;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.dom.AttributeHandler;
import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.internal.ComponentUpdateHandler;
import net.contextfw.web.application.internal.ComponentUpdateHandlerFactory;
import net.contextfw.web.application.internal.WebResponder;
import net.contextfw.web.application.internal.WebResponder.Mode;
import net.contextfw.web.application.internal.component.ComponentBuilder;
import net.contextfw.web.application.internal.component.ComponentRegister;
import net.contextfw.web.application.internal.component.WebApplicationComponent;
import net.contextfw.web.application.internal.initializer.InitializerContextImpl;
import net.contextfw.web.application.request.Request;

import com.google.inject.Inject;
import com.google.inject.Injector;

@WebApplicationScoped
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

    @Inject
    private ModuleConfiguration configuration;

    public WebApplicationImpl() {
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
                    httpContext.getResponse().sendRedirect(httpContext.getCurrentUrl());
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
                d.attr("contextPath", configuration.getContextPath());

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

                if (configuration.getXmlParamName() == null
                        || httpContext.getRequest().getParameter(configuration.getXmlParamName()) == null) {
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
    public void updateState() throws WebApplicationException {
        mode = Mode.UPDATE;
        updateElements();
    }

    protected void updateElements() throws WebApplicationException {
        try {
            Request request = new Request(httpContext.getRequest());

            String[] elementIds = request.param("el").getStringValues(null);

            if (elementIds != null && elementIds.length > 0) {

                for (String id : elementIds) {
                    String event = request.param("method").getStringValue(null);
                    Component element = componentRegister.findComponent(id);

                    String key = ComponentUpdateHandler.getKey(element.getClass(), event);

                    if (!updateHandlers.containsKey(key) || configuration.isDebugMode()) {
                        updateHandlers.put(key, euhf.createHandler(element.getClass(), event));
                    }

                    ComponentUpdateHandler handler = updateHandlers.get(key);

                    if (handler != null) {
                        handler.invoke(element, request.subRequest(element.getId()));
                    }
                }
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