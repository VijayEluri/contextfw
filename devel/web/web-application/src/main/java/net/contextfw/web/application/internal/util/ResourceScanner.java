package net.contextfw.web.application.internal.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Locates all resources with given extension from paths
 * 
 * @author marko
 * 
 */
public class ResourceScanner extends AbstractScanner {

    public static List<File> findResources(List<String> resourcePaths,
            Pattern acceptor) {
        
        List<File> files = new ArrayList<File>();

        List<File> directories = getRootFiles(resourcePaths);

        while (!directories.isEmpty()) {
            File dir = directories.remove(0);
            for (File child : dir.listFiles()) {
                if (child.isDirectory()) {
                    directories.add(child);
                } else if (acceptor.matcher(child.getName()).matches()) {
                    files.add(child);
                }
            }
        }
        
        return files;
    }
}
