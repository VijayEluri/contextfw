package net.contextfw.web.application.internal.development;

import net.contextfw.web.application.development.DevelopmentTools;

public interface InternalDevelopmentTools extends DevelopmentTools {

    ClassLoader reloadClasses();
    void reloadResources();
}
