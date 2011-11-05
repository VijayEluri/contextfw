package net.contextfw.web.commons.cloud.session;

import net.contextfw.web.application.lifecycle.PageScoped;

@PageScoped
public class CloudSessionHolder {

    private String handle;
    
    private boolean isOpen = false;;
    
    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }
}
