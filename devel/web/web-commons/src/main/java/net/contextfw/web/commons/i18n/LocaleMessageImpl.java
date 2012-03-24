package net.contextfw.web.commons.i18n;

import net.contextfw.web.application.component.Buildable;
import net.contextfw.web.application.component.CustomBuild;
import net.contextfw.web.application.component.DOMBuilder;
import net.contextfw.web.application.scope.Provided;

@Buildable(wrap=false)
class LocaleMessageImpl implements LocaleMessage {

    @Provided
    private final LocaleServiceImpl service;
    
    private final String name;
    
    LocaleMessageImpl(LocaleServiceImpl service, String name) {
        this.service = service;
        this.name = name;
    }
    
    @CustomBuild(wrap = false)
    public void build(DOMBuilder b) {
        b.text(toString());
    }
    
    public String toString() {
        return service.getText(name);
    }
}
