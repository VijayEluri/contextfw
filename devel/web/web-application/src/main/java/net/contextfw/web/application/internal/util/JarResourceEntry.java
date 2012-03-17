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
