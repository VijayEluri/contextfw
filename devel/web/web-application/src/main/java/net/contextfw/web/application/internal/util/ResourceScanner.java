package net.contextfw.web.application.internal.util;

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

    public static List<ResourceEntry> findResources(List<String> resourcePaths,
            Pattern acceptor) {
        
        List<ResourceEntry> entries = findResourceEntries(resourcePaths);
        List<ResourceEntry> rv = new ArrayList<ResourceEntry>();
        
        for (ResourceEntry entry : entries) {
            if (acceptor.matcher(entry.getPath()).matches()) {
                rv.add(entry);
            }
        }        
        
        return rv;
    }
}
