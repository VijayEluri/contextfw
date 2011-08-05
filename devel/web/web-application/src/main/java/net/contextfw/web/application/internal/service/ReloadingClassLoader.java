package net.contextfw.web.application.internal.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.contextfw.web.application.WebApplicationException;

import org.apache.commons.io.IOUtils;

public class ReloadingClassLoader extends ClassLoader {

    private final ReloadingClassLoaderConf conf;

    private final Map<String, Class<?>> cache = new HashMap<String, Class<?>>();

    public ReloadingClassLoader(ReloadingClassLoaderConf conf) {
        super(Thread.currentThread().getContextClassLoader());
        this.conf = conf;
        for (Class<?> cl : conf.getExcludedClasses()) {
            cache.put(cl.getCanonicalName(), cl);
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (conf.isInReloadablePackage(name)) {
            if (!cache.containsKey(name)) {
                cache.put(name, findReloadableClass(name));
            }
            return cache.get(name);
        } else {
            return super.loadClass(name);
        }
    }

    public Class<?> findReloadableClass(String s) throws ClassNotFoundException {
        try {
            byte[] bytes = loadClassData(s);
            if (bytes != null) {
                return defineClass(s, bytes, 0, bytes.length);
            } else {
                return super.loadClass(s);
            }
        } catch (LinkageError le) {
            throw new WebApplicationException(
                    "Linkage error while trying to load class '" + s +
                            "' from reloadable package. Make sure that" +
                            " class is not accessed from non-reloadable package.",
                    le);
        } catch (IOException ioe) {
            return super.loadClass(s);
        }
    }

    private byte[] loadClassData(String className) throws IOException {
        InputStream stream = super.getResourceAsStream(className.replaceAll("\\.", "/") + ".class");
        if (stream != null) {
            DataInputStream dis = new DataInputStream(stream); // NOSONAR
            byte buff[] = IOUtils.toByteArray(stream);
            dis.close();
            return buff;
        } else {
            return null;
        }
    }
}
