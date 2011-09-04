package net.contextfw.web.commons.i18n;

import net.contextfw.web.application.component.Buildable;
import net.contextfw.web.application.component.CustomBuild;
import net.contextfw.web.application.component.DOMBuilder;

@Buildable(wrap=false)
class LocaleMessageImpl implements LocaleMessage {

    private final LocaleServiceImpl service;
    private final String name;
    
    LocaleMessageImpl(LocaleServiceImpl service, String name) {
        this.service = service;
        this.name = name;
    }
    
    @CustomBuild(wrap = false)
    public void build(DOMBuilder b) {
        b.text(service.getText(name));
    }
}
