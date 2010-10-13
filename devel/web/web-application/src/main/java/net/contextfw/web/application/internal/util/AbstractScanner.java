package net.contextfw.web.application.internal.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.contextfw.web.application.WebApplicationException;

/**
 * Locates all resources with given extension from paths
 * 
 * @author marko
 * 
 */
public class AbstractScanner {

    private static final String FILE = "file";
    private static final String CLASSPATH = "classpath";
    
    static List<File> getRootFiles(List<String> resourcePaths) {
        List<URI> rootURIs = toURIs(resourcePaths);
        Set<File> rootFiles = new LinkedHashSet<File>(rootURIs.size());
        
        try {
            for (URI rootURI : rootURIs) {
                if (FILE.equals(rootURI.getScheme())) {
                    
                    File file = new File(rootURI.getSchemeSpecificPart());
                    if (file.isDirectory()) {
                        rootFiles.add(file);
                    } else {
                        throw new WebApplicationException("URI " + rootURI.toString() + " is not a directory");
                    }
                    
                } else if (CLASSPATH.equals(rootURI.getScheme())) {
                    
                    Enumeration<URL> resources = AbstractScanner.class.getClassLoader()
                        .getResources(rootURI.getSchemeSpecificPart());
                    
                    while (resources.hasMoreElements()) {
                        URL resource = resources.nextElement();
                        File file = new File(URLDecoder.decode(resource.getFile(), "UTF-8"));
                        if (file.isDirectory()) {
                            rootFiles.add(file);
                        } else {
                            throw new WebApplicationException("URI " + rootURI.toString() + " is not a directory");
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new WebApplicationException(e);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
        
        List<File> rv = new ArrayList<File>();
        rv.addAll(rootFiles);
        
        return rv;
    }

    public static List<URI> toURIs(List<String> resourcePaths) {
        List<URI> roots = new ArrayList<URI>(resourcePaths.size());

        for (String path : resourcePaths) {
            int separator = path.indexOf(":");

            try {
                if (separator == -1) {
                    roots.add(new URI("classpath", path.replaceAll("\\.", "/"),
                            null));
                } else {
                    
                    String scheme = path.substring(0, separator);
                    String ssp = path.substring(separator+1);
                    
                    if (!CLASSPATH.equals(scheme) && !FILE.equals(scheme)) {
                        throw new WebApplicationException("Scheme '" + scheme 
                                + "' is not supported. Path was: " + path);
                    }
                    
                    roots.add(new URI(scheme, ssp, null));
                }
            } catch (URISyntaxException e) {
                throw new WebApplicationException(e);
            }
        }

        return roots;
    }
}