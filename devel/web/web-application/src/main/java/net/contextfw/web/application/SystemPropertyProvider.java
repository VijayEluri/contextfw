package net.contextfw.web.application;

import java.util.Properties;

/**
 * The default property provider.
 *
 * <p>
 *  Returns properties from system properties
 * </p>
 */
public class SystemPropertyProvider implements PropertyProvider {

    @Override
    public Properties get() {
        return System.getProperties();
    }

}
