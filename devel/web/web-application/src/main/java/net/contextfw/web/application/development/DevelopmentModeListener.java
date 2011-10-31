package net.contextfw.web.application.development;

public interface DevelopmentModeListener {
    void classesReloaded(ClassLoader classLoader);
    void resourcesReloaded();
}
