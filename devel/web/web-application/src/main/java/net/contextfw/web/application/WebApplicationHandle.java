package net.contextfw.web.application;

import java.io.Serializable;

public class WebApplicationHandle implements Serializable {

    private static final long serialVersionUID = -2578266439991410555L;

    private final String key;

    public WebApplicationHandle(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other instanceof WebApplicationHandle) {
            WebApplicationHandle otherHandle = (WebApplicationHandle) other;
            return this.key.equals(otherHandle.key);
        }
        else {
            return false;
        }
    }
}