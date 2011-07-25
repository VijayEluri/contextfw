package net.contextfw.web.application.internal.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import net.contextfw.web.application.configuration.Configuration;
import static net.contextfw.web.application.configuration.Configuration.*;

import org.junit.Before;
import org.junit.Test;

public class ReloadingClassLoaderTest {

    private Configuration conf;
    
    @Before
    public void setup() {
        conf = Configuration.getDefaults()
                .add(VIEW_COMPONENT_ROOT_PACKAGE, "net.contextfw.views");
    }
    
    @Test
    public void Build_Paths() {
        conf = conf
            .add(BUILD_PATH, "target/classes")
            .add(BUILD_PATH, "target/test-classes");
        
        ReloadingClassLoaderConf rConf = new ReloadingClassLoaderConf(conf);
        assertEquals(2, rConf.getBuildPaths().size());
        
        assertTrue(rConf.getBuildPaths().contains("target/classes/"));
    }
    
    @Test
    public void Reloadable_Packages() {
        
        conf = conf
            .add(RELOADABLE_CLASSES.includedPackage("net.contextfw", false))
            .add(RELOADABLE_CLASSES.includedPackage("net.contextfw.web2", false))
            .add(RELOADABLE_CLASSES.includedPackage("net.contextfw", false))
            .add(RELOADABLE_CLASSES.includedPackage("net.contextfw.web"));

        ReloadingClassLoaderConf rConf = new ReloadingClassLoaderConf(conf);
        
        assertTrue(rConf.isInReloadablePackage("net.contextfw.Link"));
        assertTrue(rConf.isInReloadablePackage("net.contextfw.web.SomeClass"));
        assertTrue(rConf.isInReloadablePackage("net.contextfw.web2.SomeClass"));
        assertTrue(rConf.isInReloadablePackage("net.contextfw.web.sub.SomeClass"));
        assertTrue(rConf.isInReloadablePackage("net.contextfw.views.SomeClass"));
        assertTrue(rConf.isInReloadablePackage("net.contextfw.views.sub.SomeClass"));
        
        assertFalse(rConf.isInReloadablePackage("net.contextfw.a.Link"));
        assertFalse(rConf.isInReloadablePackage("net.contextfw.web2.sub.SomeClass"));
        assertFalse(rConf.isInReloadablePackage("net.contextfw.web2.sub.sab.SomeClass"));
        
        Set<String> names = rConf.getReloadablePackageNames();
        assertTrue(names.contains("net.contextfw.web"));
        assertTrue(names.contains("net.contextfw.views"));
    }
    
    @Test
    public void Excluded_Classes() {
        conf = conf
            .add(RELOADABLE_CLASSES.excludedClass(String.class))
            .add(RELOADABLE_CLASSES.excludedClass(ReloadingClassLoader.class));
        
        ReloadingClassLoaderConf rConf = new ReloadingClassLoaderConf(conf);
        Set<Class<?>> classes = rConf.getExcludedClasses();
        assertEquals(2, classes.size());
        assertTrue(classes.contains(String.class));
        assertTrue(classes.contains(ReloadingClassLoader.class));
    }
    
    @Test
    public void Load_Classes() throws ClassNotFoundException {
        conf = conf
            .add(BUILD_PATH, "target/classes")
            .add(BUILD_PATH, "target/test-classes")
            .add(RELOADABLE_CLASSES.includedPackage("net.contextfw.web.application.internal.service", false))
            .add(RELOADABLE_CLASSES.excludedClass(NonReloadable.class));
        
        ReloadingClassLoader classLoader = new ReloadingClassLoader(
                new ReloadingClassLoaderConf(conf));
        
        Class<?> reloadable = 
                classLoader.loadClass("net.contextfw.web.application.internal.service.Reloadable");
        
        Class<?> nonReloadable = 
                classLoader.loadClass("net.contextfw.web.application.internal.service.NonReloadable");
        
        assertEquals(NonReloadable.class, nonReloadable);
        assertNotSame(Reloadable.class, reloadable);
    }
    
    @Test(expected=ClassNotFoundException.class)
    public void Throw_Class_CastException() throws ClassNotFoundException {
        conf = conf
            .add(BUILD_PATH, "target/classes")
            .add(BUILD_PATH, "target/test-classes")
            .add(RELOADABLE_CLASSES.includedPackage("net.contextfw.web.application.internal.service", false))
            .add(RELOADABLE_CLASSES.excludedClass(NonReloadable.class));
        
        ReloadingClassLoader classLoader = new ReloadingClassLoader(
                new ReloadingClassLoaderConf(conf));
        
        classLoader.loadClass("net.contextfw.web.application.internal.service.Reloadaple");
    }
}
 