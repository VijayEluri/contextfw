package net.contextfw.web.application.internal.servlet;

import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Assert;

import org.junit.Test;

public class UriMappingTest {
    
    @Test
    public void Test1() {
        SortedSet<UriMapping> mappings = new TreeSet<UriMapping>();
        mappings.add(mapping("/a"));
        mappings.add(mapping("/b"));
        mappings.add(mapping("/b/a*"));
        mappings.add(mapping("regex:/admin/aa"));
        mappings.add(mapping("regex:/admin[/]?"));
        assertContainsPath(mappings, "/a");
        assertContainsPath(mappings, "/b");
        assertContainsPath(mappings, "/b/a*");
        assertContainsPath(mappings, "/admin/aa");
        assertContainsPath(mappings, "/admin[/]?");
    }
    
    private void assertContainsPath(SortedSet<UriMapping> mappings, String path) {
        for (UriMapping mapping : mappings) {
            if (mapping.getPath().equals(path)) {
                return;
            }
        }
        
        Assert.fail("No path found for : " + path);
    }
    
    private UriMapping mapping(String path) {
        return new UriMapping(null, path, null);
    }

}
