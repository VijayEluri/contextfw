package net.contextfw.web.application.internal.service;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.contextfw.web.application.WebApplicationException;

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
        File f = null;
        for (String prefix : conf.getBuildPaths()) {
            f = new File(prefix + className.replaceAll("\\.", "/") + ".class");
            if (f.exists()) {
                break;
            } else {
                f = null;
            }
        }
        if (f != null) {
            int size = (int) f.length();
            byte buff[] = new byte[size];
            FileInputStream fis = new FileInputStream(f);
            DataInputStream dis = new DataInputStream(fis);
            dis.readFully(buff);
            dis.close();
            return buff;
        } else {
            return null;
        }
    }
}
