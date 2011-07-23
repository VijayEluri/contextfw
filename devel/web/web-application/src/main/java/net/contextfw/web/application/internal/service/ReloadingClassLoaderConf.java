package net.contextfw.web.application.internal.service;

import java.util.List;
import java.util.Set;

public class ReloadingClassLoaderConf {

    private final Set<String> reloadablePackages;
    
    private final List<String> classDirectories;

    public ReloadingClassLoaderConf(Set<String> reloadablePackages, 
                                    List<String> classDirectories) {
        
        this.reloadablePackages = reloadablePackages;
        this.classDirectories = classDirectories;
    }

    public Set<String> getReloadablePackages() {
        return reloadablePackages;
    }

    public List<String> getClassDirectories() {
        return classDirectories;
    }
}