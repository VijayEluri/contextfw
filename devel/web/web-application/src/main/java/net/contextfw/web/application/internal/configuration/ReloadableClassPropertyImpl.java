package net.contextfw.web.application.internal.configuration;

import java.util.HashSet;
import java.util.Set;

import net.contextfw.web.application.configuration.ReloadableClassProperty;

import org.apache.commons.lang.StringUtils;

public class ReloadableClassPropertyImpl extends SelfSetPropertyImpl<Object> implements ReloadableClassProperty {

    public ReloadableClassPropertyImpl(String key) {
        super(key);
    }
    
    private ReloadableClassPropertyImpl(String key, Object value) {
        super(key, value);
    }

    @Override
    public ReloadableClassProperty includedPackage(String name) {
        return includedPackage(name, true);
    }

    @Override
    public ReloadableClassProperty includedPackage(String name, boolean recursive) {
        String trimmedName = StringUtils.trimToNull(name);
        if (trimmedName== null) {
            throw new IllegalArgumentException("Package name cannot be empty");
        }
        return new ReloadableClassPropertyImpl(getKey(), trimmedName + ":" + recursive);
    }

    @Override
    public ReloadableClassProperty excludedClass(Class<?>... clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Classes cannot be null");
        }
        Set<Class<?>> classes = new HashSet<Class<?>>();
        for (Class<?> cl : clazz) {
            classes.add(cl);
        }
        return new ReloadableClassPropertyImpl(getKey(), classes); 
    }

}
 