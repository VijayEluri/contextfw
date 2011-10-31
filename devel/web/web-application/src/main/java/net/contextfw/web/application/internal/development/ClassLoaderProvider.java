package net.contextfw.web.application.internal.development;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class ClassLoaderProvider {

    private final ReloadingClassLoaderConf conf;
    
    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    
    public ClassLoaderProvider(ReloadingClassLoaderConf conf) {
        this.conf = conf;
    }
    
    public boolean isReloadable() {
        return conf != null;
    }
    
    public ClassLoader get() {
        return classLoader;
    }
    
    public ClassLoader reload() {
        if (isReloadable() && conf != null) {
            classLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                @Override
                public ClassLoader run() {
                    return new ReloadingClassLoader(conf);
                }
            });
        }
        return classLoader;
    }
}
