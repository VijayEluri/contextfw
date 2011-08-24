package net.contextfw.web.application.internal.component;

import net.contextfw.web.application.component.Component;

import com.google.inject.Inject;
import com.google.inject.spi.InjectionListener;

public class AutoRegisterListener<I extends Component> implements InjectionListener<I> {

    private ComponentBuilder componentBuilder;
    
    @Override
    public void afterInjection(I injectee) {
        componentBuilder.getMetaComponent(injectee.getClass()).registerChildren(injectee);
    }

    @Inject
    public void setComponentBuilder(ComponentBuilder componentBuilder) {
        this.componentBuilder = componentBuilder;
    }
}
