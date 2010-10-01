package net.contextfw.web.application;

import java.io.Serializable;

public class WebApplicationHandle implements Serializable {

    private static final long serialVersionUID = -2578266439991410555L;

    private String key;

    public WebApplicationHandle(String handle) {
        this.key = handle;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WebApplicationHandle other = (WebApplicationHandle) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        }
        else if (!key.equals(other.key))
            return false;
        return true;
    }
}