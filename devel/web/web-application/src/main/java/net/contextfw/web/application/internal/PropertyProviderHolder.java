package net.contextfw.web.application.internal;

import net.contextfw.web.application.PropertyProvider;

import com.google.inject.Inject;

public class PropertyProviderHolder {

    @Inject
    private PropertyProvider propertyProvider;

    public PropertyProvider getPropertyProvider() {
        return propertyProvider;
    }
    
}
