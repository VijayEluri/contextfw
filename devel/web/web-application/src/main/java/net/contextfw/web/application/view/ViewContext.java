package net.contextfw.web.application.view;

import java.util.Locale;

import net.contextfw.web.application.component.Component;

public interface ViewContext {
    Class<? extends Component> getChildClass();
    Component initChild();
    void setLocale(Locale locale);
}