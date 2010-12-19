package net.contextfw.web.application.internal.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import net.contextfw.web.application.WebApplicationException;

public class FileResourceEntry extends ResourceEntry {

    private final File file;
    
    public FileResourceEntry(String path, File file) {
        super(path);
        this.file = file;
    }

    @Override
    public InputStream getInputStream() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new WebApplicationException(e);
        }
    }
}
