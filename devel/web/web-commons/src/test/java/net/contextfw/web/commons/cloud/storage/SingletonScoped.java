package net.contextfw.web.commons.cloud.storage;

import com.google.inject.Singleton;

@Singleton
public class SingletonScoped {

    private final String msg;

    public SingletonScoped(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
    
}
