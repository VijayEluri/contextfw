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

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.contextfw.web.application.WebApplicationException;

import org.junit.Test;

public class ResourceScannerTest {

    @Test
    public void testValidRoots() {
        List<String> paths = new ArrayList<String>();
        
        paths.add("net.contextfw.web");
        paths.add("file:foo/bar");
        paths.add("classpath:net/contextfw/web");
        
        List<URI> roots = ResourceScanner.toURIs(paths);
        
        assertEquals("classpath:net/contextfw/web", roots.get(0).toString());
        assertEquals("file:foo/bar", roots.get(1).toString());
        assertEquals("classpath:net/contextfw/web", roots.get(2).toString());
    }
    
    @Test(expected=WebApplicationException.class)
    public void testInvalidRoots() {
        List<String> paths = new ArrayList<String>();
        paths.add("http://www.contextfw.net");
        ResourceScanner.toURIs(paths);
    }
    
    @Test
    public void testRootFiles() {
        
        List<String> paths = new ArrayList<String>();
        
        paths.add("net.contextfw.web.application.internal.scope");
        //paths.add("javax.servlet");
        
        paths.add("file:src/main/resources");
        
        // This is a duplicate of the first package and should get ignored
        paths.add("classpath:net/contextfw/web/application/internal/scope");
        
        List<ResourceEntry> rootFiles = AbstractScanner.findResourceEntries(paths);
        assertEquals(3, rootFiles.size());
        
//        assertTrue(rootFiles.get(0).getAbsolutePath().endsWith("/target/test-classes/net/contextfw/web"));
//        assertTrue(rootFiles.get(1).getAbsolutePath().endsWith("/target/classes/net/contextfw/web"));
//        assertTrue(rootFiles.get(2).getAbsolutePath().endsWith("/src/main/resources"));
    }
    
    @Test
    public void testFindResources() {
        List<String> paths = new ArrayList<String>();
        paths.add("net.contextfw.web.application.internal.util");
        
//        List<File> files = ResourceScanner.findResources(paths, 
//                Pattern.compile(".*\\.class"));
//        
//        for (File file : files) {
//            System.out.println(file.getName());
//        }
    }
}