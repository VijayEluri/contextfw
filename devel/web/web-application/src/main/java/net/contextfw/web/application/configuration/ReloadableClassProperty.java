package net.contextfw.web.application.configuration;

import java.util.Set;

public interface ReloadableClassProperty extends SelfAddableProperty<Set<Object>, Object> {

    /**
     * Includes packages containing reloadable classes.
     * 
     * <p>
     *  This method is recursive and includes sub packages automatically.
     * </p>
     * 
     * @param name
     *      The name of the root package
     * @return
     *      The property
     */
    ReloadableClassProperty includedPackage(String name);

    /**
     * Includes packages containing reloadable classes
     * 
     * @param name
     *  The name of the package
     * @param recursive
     *  Defines if sub packages should be included also
     * @return
     *  The property
     */
    ReloadableClassProperty includedPackage(String name, boolean recursive);
    
    /**
     * Excludes classes from being reloaded in reloadable packages.
     * 
     * @param clazz
     *   The classes to be excluded from reloading
     * @return
     */
    ReloadableClassProperty excludedClass(Class<?>... clazz);
    
}
