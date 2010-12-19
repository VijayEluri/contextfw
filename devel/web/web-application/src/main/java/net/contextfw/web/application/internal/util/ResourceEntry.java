package net.contextfw.web.application.internal.util;

import java.io.InputStream;

public abstract class ResourceEntry {

    protected String path;
    
    protected ResourceEntry() {
    }
    
    protected ResourceEntry(String path) {
        this.path = path;
    }
    
    public abstract InputStream getInputStream();
    
    public String getPath() {
        return path;
    }
}
