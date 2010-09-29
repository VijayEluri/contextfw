package net.contextfw.web.application.initializer;

import java.util.Locale;

import net.contextfw.web.application.elements.CElement;

public interface InitializerContext {
    Class<? extends CElement> getChildClass();
    CElement initChild();
    void sendRedirect(String url);
    void sendError(int code);
    void sendError(int code, String msg);
    void setLocale(Locale locale);
}