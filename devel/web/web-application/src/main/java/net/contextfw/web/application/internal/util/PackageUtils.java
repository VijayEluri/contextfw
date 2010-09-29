package net.contextfw.web.application.internal.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class PackageUtils {

    public static List<File> getResources(String packageName, ClassLoader classLoader) throws IOException {
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(URLDecoder.decode(resource.getFile(), "UTF-8")));
        }
        ArrayList<File> files = new ArrayList<File>();
        for (File directory : dirs) {
            files.addAll(findResources("", directory, packageName));
        }
        return files;
    }
    
    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static List<Class<?>> getClasses(String packageName, ClassLoader classLoader)
            throws ClassNotFoundException, IOException 
    {
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(URLDecoder.decode(resource.getFile(), "UTF-8")));
        }
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        for (File directory : dirs) {
            classes.addAll(findClasses("", directory, packageName));
        }
        return classes;
    }

    private static List<File> findResources(String parent, File directory, String packageName) 
    {
        List<File> files1 = new ArrayList<File>();
        if (!directory.exists()) {
            return files1;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            
            String fileName = file.getName();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
                files1.addAll(findResources(parent+"/"+fileName, file, packageName + "." + fileName));
            } else if (!fileName.endsWith(".class")) {
                files1.add(file);
            }
        }
        return files1;
    }
    
    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> findClasses(String parent, File directory, String packageName) throws ClassNotFoundException 
    {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            
            String fileName = file.getName();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
                classes.addAll(findClasses(parent+"/"+fileName, file, packageName + "." + fileName));
            } else if (fileName.endsWith(".class") && !fileName.contains("$")) {
                Class<?> _class;
                try {
                    _class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
                } catch (ExceptionInInitializerError e) {
                    // happen, for example, in classes, which depend on 
                    // Spring to inject some beans, and which fail, 
                    // if dependency is not fulfilled
                    _class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6),
                            false, Thread.currentThread().getContextClassLoader());
                }
                classes.add(_class);
            }
        }
        return classes;
    }
}
