/**
 * Copyright 2010 Marko Lavikainen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.contextfw.web.application.internal.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.contextfw.web.application.WebApplicationException;

/**
 * Locates all resources with given extension from paths
 * 
 * @author marko
 * 
 */
public abstract class AbstractScanner {

    private static final String FILE = "file";
    private static final String CLASSPATH = "classpath";
    
    protected AbstractScanner() {
    }
    
    protected static List<ResourceEntry> findResourceEntries(List<String> resourcePaths) {
        List<ResourceEntry> entries = new ArrayList<ResourceEntry>();
        List<URI> rootURIs = toURIs(resourcePaths);
        try {
        for (URI rootURI : rootURIs) {
            if (FILE.equals(rootURI.getScheme())) {
                entries.addAll(findResourcesFromFilesystem(rootURI));
            } else if (CLASSPATH.equals(rootURI.getScheme())) {
                Enumeration<URL> resources = Thread.currentThread().getContextClassLoader()
                    .getResources(rootURI.getSchemeSpecificPart());
                    while (resources.hasMoreElements()) {
                        URL resource = resources.nextElement();
                        if ("file".equals(resource.getProtocol())) {
                            entries.addAll(findResourcesFromFilesystem(rootURI.getSchemeSpecificPart(), resource));
                        } else if ("jar".equals(resource.getProtocol())) {
                            entries.addAll(findResourcesFromJar(resource));
                        } else {
                            throw new WebApplicationException("Protocol " + resource.getProtocol() + " is not supported");
                        }
                    }
            }
        }
            } catch (IOException e) {
                throw new WebApplicationException(e);
            } catch (URISyntaxException e) {
                throw new WebApplicationException(e);
            }
        
        return entries;
    }
    
    private static Collection<? extends ResourceEntry> findResourcesFromJar(URL directory) throws IOException {
        
        List<ResourceEntry> resources = new ArrayList<ResourceEntry>();
        
        String jarPath = directory.getPath().substring(5, directory.getPath().indexOf("!"));
        String path = directory.getPath().substring(directory.getPath().indexOf("!") + 2);
        
        JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
        
        Enumeration<JarEntry> entries = jar.entries();
        
        while(entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().startsWith(path) && !entry.isDirectory()) {
                resources.add(new JarResourceEntry(jar, entry));
            }
        }
        
        return resources;
    }

    private static Collection<? extends ResourceEntry> findResourcesFromFilesystem(String pathPrefix, URL rootUrl) throws URISyntaxException, FileNotFoundException, UnsupportedEncodingException {
        File rootDirectory = new File(URLDecoder.decode(rootUrl.getFile(), "UTF-8"));
        if (rootDirectory.isDirectory()) {
            return findResourcesFromFilesystem(pathPrefix+"/", rootDirectory);
        } else {
            return Collections.emptyList();
        }
    }
        
    private static Collection<? extends ResourceEntry> findResourcesFromFilesystem(URI rootURI) throws FileNotFoundException {
        File rootDirectory = new File(rootURI.getSchemeSpecificPart());
        if (!rootDirectory.isDirectory()) {
            throw new WebApplicationException("File " + rootDirectory.getAbsolutePath() + " is not a directory");
        } else {
            return findResourcesFromFilesystem(rootURI.getSchemeSpecificPart()+"/", rootDirectory);
        }

    }
    
    private static Collection<? extends ResourceEntry> findResourcesFromFilesystem(String pathPrefix, File rootDirectory) throws FileNotFoundException {
        List<ResourceEntry> entries = new ArrayList<ResourceEntry>();
        if (!rootDirectory.exists()) {
            throw new WebApplicationException("Directory " + rootDirectory.getAbsolutePath() + " does not exist");
        }
        
        int length = rootDirectory.getPath().length() + 1;
        
        List<File> directories = new ArrayList<File>();
        directories.add(rootDirectory);
        while (!directories.isEmpty()) {
            File dir = directories.remove(0);
            for (File child : dir.listFiles()) {
                if (child.isDirectory()) {
                    directories.add(child);
                } else {
                    entries.add(new FileResourceEntry(pathPrefix+child.getPath().substring(length), child));
                }
            }
        }
        
        return entries;
    }

    public static List<URI> toURIs(Collection<String> resourcePaths) {
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