package net.contextfw.web.application.initializer;

import java.util.Locale;

import net.contextfw.web.application.component.Component;

public interface InitializerContext {
    Class<? extends Component> getChildClass();
    Component initChild();
    void setLocale(Locale locale);
}