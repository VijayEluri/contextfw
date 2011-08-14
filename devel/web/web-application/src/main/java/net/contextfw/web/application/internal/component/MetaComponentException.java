package net.contextfw.web.application.internal.component;

import net.contextfw.web.application.remote.ErrorResolution;

public class MetaComponentException extends RuntimeException {

    private final ErrorResolution resolution;
    
    private static final long serialVersionUID = 1L;

    public MetaComponentException(ErrorResolution resolution) {
        this.resolution = resolution;
    }

    public ErrorResolution getResolution() {
        return resolution;
    }
    
}
