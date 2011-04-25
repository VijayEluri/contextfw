package net.contextfw.web.application;

import java.util.Properties;

public class SystemPropertyProvider implements PropertyProvider {

    @Override
    public Properties get() {
        return System.getProperties();
    }

}
