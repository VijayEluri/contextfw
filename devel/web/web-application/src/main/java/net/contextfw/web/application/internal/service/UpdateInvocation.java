package net.contextfw.web.application.internal.service;

public class UpdateInvocation {

    public static final UpdateInvocation DELAYED = new UpdateInvocation(true, false, null);
    public static final UpdateInvocation NOT_DELAYED = new UpdateInvocation(false, false, null);
    
    private final boolean delayed;
    private final boolean resource;
    private final Object retVal;
    
    private UpdateInvocation(boolean delayed, boolean resource, Object retVal) {
        this.resource = resource;
        this.retVal = retVal;
        this.delayed = delayed;
    }

    public UpdateInvocation(boolean resource, Object retVal) {
        this(false, resource, retVal);
    }
    
    public boolean isResource() {
        return resource;
    }

    public Object getRetVal() {
        return retVal;
    }

    public boolean isDelayed() {
        return delayed;
    }
}
