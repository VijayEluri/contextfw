package net.contextfw.web.application.internal.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.internal.util.ResourceScanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

@Singleton
public class DirectoryWatcher {

    private Logger logger = LoggerFactory.getLogger(DirectoryWatcher.class);

    private Pattern matcher;

    public DirectoryWatcher(Collection<String> paths, Pattern matcher) {
        if (paths != null) {
            this.matcher = matcher;
            logger.info("Tracking resources for changes");
            List<URI> uris = ResourceScanner.toURIs(paths);
            for (URI uri : uris) {
                Enumeration<URL> resources;
                try {
                    resources = Thread.currentThread().getContextClassLoader()
                            .getResources(uri.getSchemeSpecificPart());
                    while (resources.hasMoreElements()) {
                        URL url = resources.nextElement();
                        File dir = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
                        if (dir.exists() && dir.isDirectory()) {
                            logger.info("Tracking directory:" + dir.getAbsolutePath());
                            rootDirs.add(dir);
                            update(dir);
                        }
                    }
                } catch (IOException e1) {
                    throw new WebApplicationException(e1);
                }
            }
        }
    }

    private static class FileEntry {

        long lastModified;
        long fileCount;

        FileEntry(File file) {
            lastModified = file.lastModified();
            if (file.isDirectory()) {
                fileCount = file.list().length;
            }
        }

        boolean hasChanged(File file) {
            if (file.isFile() && file.lastModified() != lastModified) {
                return true;
            } else if (file.isDirectory() && fileCount != file.list().length) {
                return true;
            } else {
                return false;
            }
        }
    }

    private final Set<File> rootDirs = new HashSet<File>();
    private final Map<File, FileEntry> entries = new HashMap<File, FileEntry>();

    public void registerDirectory(File file) {

        if (file == null) {
            throw new IllegalArgumentException("Cannot register null directory");
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("File " + file.getAbsolutePath()
                    + " is not a directory");
        }

    }

    private void update(File file) {
        if (file != null && file.exists()) {
            entries.put(file, new FileEntry(file));
            if (file.isDirectory()) {
                for (File child : file.listFiles()) {
                    update(child);
                }
            }
        }
    }

    private boolean hasChanged(File file) {
        if (file.isFile() && !matcher.matcher(file.getName()).matches()) {
            return false;
        } else if (!file.exists()) {
            return true;
        }
        FileEntry entry = entries.get(file);
        if (entry == null) {
            return true;
        } else if (entry.hasChanged(file)) {
            return true;
        } else if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                if (hasChanged(child)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasChanged() {
        for (File root : rootDirs) {
            if (hasChanged(root)) {
                reload();
                return true;
            }
        }
        return false;
    }

    private void reload() {
        entries.clear();
        for (File root1 : rootDirs) {
            update(root1);
        }
    }
}
