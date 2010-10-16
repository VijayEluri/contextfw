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
import net.contextfw.web.application.dom.AttributeHandler;
import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CElement;
import net.contextfw.web.application.internal.ElementRegister;
import net.contextfw.web.application.internal.ElementUpdateHandler;
import net.contextfw.web.application.internal.ElementUpdateHandlerFactory;
import net.contextfw.web.application.internal.WebApplicationElement;
import net.contextfw.web.application.internal.WebResponder;
import net.contextfw.web.application.internal.WebResponder.Mode;
import net.contextfw.web.application.internal.initializer.InitializerContextImpl;
import net.contextfw.web.application.request.Request;

import com.google.inject.Inject;
import com.google.inject.Injector;

@WebApplicationScoped
public class WebApplicationImpl implements WebApplication {

    @Inject
    private ElementUpdateHandlerFactory euhf;

    private static volatile Map<String, ElementUpdateHandler> updateHandlers = new HashMap<String, ElementUpdateHandler>();

    @Inject
    private Injector injector;

    @Inject
    private HttpContext httpContext;

    private final ElementRegister elementRegister = new ElementRegister();

    private final WebApplicationElement rootElement =
            new WebApplicationElement(elementRegister);

    private List<Class<? extends CElement>> chain;

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
        rootElement.registerChild(context.initChild());
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
                    d = new DOMBuilder("WebApplication", attributes);
                } else {
                    d = new DOMBuilder("WebApplication.update", attributes);
                }

                d.attr("handle", webApplicationHandle.getKey());
                d.attr("contextPath", configuration.getContextPath());

                if (context.getLocale() != null) {
                    d.attr("xml:lang", context.getLocale().toString());
                }
                if (mode == Mode.INIT) {
                    rootElement.build(d);
                } else if (httpContext.getRedirectUrl() != null) {
                    d.descend("Redirect").attr("href", httpContext.getRedirectUrl());
                } else if (httpContext.getErrorCode() != null) {
                    d.descend("Error").attr("code", httpContext.getErrorCode()).text(httpContext.getErrorMsg());
                } else if (httpContext.isReload()) {
                    d.descend("Reload");
                } else {
                    rootElement.doCascadedUpdate(d);
                }

                rootElement.clearCascadedUpdate();

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
                    CElement element = elementRegister.findElement(id);

                    String key = ElementUpdateHandler.getKey(element.getClass(), event);

                    if (!updateHandlers.containsKey(key) || configuration.isDebugMode()) {
                        updateHandlers.put(key, euhf.createHandler(element.getClass(), event));
                    }

                    ElementUpdateHandler handler = updateHandlers.get(key);

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
    public void setInitializerChain(List<Class<? extends CElement>> chain) {
        this.chain = chain;
    }
}