package net.contextfw.web.application.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.contextfw.web.application.WebApplicationException;

public class JarResourceEntry extends ResourceEntry {

    private final JarFile file;
    private final JarEntry entry;
    
    public JarResourceEntry(JarFile file, JarEntry entry) {
        super(entry.getName());
        this.file = file;
        this.entry = entry;
    }

    @Override
    public InputStream getInputStream() {
        try {
            return file.getInputStream(entry);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
    }

}
