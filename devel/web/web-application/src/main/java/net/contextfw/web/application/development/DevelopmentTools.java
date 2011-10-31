package net.contextfw.web.application.development;

public interface DevelopmentTools {
    void addListener(DevelopmentModeListener listener);
    boolean isDevelopmentMode();
}
