package net.contextfw.web.application.initializer;

import java.util.Locale;

import net.contextfw.web.application.elements.CElement;

public interface InitializerContext {
    Class<? extends CElement> getChildClass();
    CElement initChild();
    void setLocale(Locale locale);
}