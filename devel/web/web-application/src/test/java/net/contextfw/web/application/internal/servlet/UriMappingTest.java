package net.contextfw.web.application.internal.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Assert;
import net.contextfw.web.application.WebApplicationException;

import org.junit.Test;

public class UriMappingTest {
    
    private UriMappingFactory fact = new UriMappingFactory();
    private UriMapping engine1 = mapping("regex:/engine/<id>/mode/<mode:(start|stop)>");
    private UriMapping engine2 = mapping("regex:<id>/mode/<mode:(start|stop)>");
    private UriMapping calendar = mapping("regex:/calendar(/<year:\\d{4}>/<month:\\d{1,2}>)?");
    private UriMapping optional = mapping("regex:/engine/mode/(<a:start>|<b:stop>)");
    
    private UriMapping user1 = mapping("/user/<id>");
    private UriMapping user2 = mapping("/user/<id>/(me/<foo>");
    
    @Test
    public void Map_Paths() {
        assertEquals("/engine/[^/]+/mode/(start|stop)", engine1.getPath());
        assertEquals("[^/]+/mode/(start|stop)", engine2.getPath());
        assertEquals("/user/*", user1.getPath());
        assertEquals("/user/*/\\(me/*", user2.getPath());
    }
    
    @Test
    public void Find_Engine1_Values() {
        String path = "/engine/12/mode/start";
        assertEquals("12", engine1.findValue(path, "id"));
        assertEquals("start", engine1.findValue(path, "mode"));
        assertNull(engine1.findValue(path, "foo"));
    }
    
    @Test
    public void Find_Engine2_Values() {
        String path = "12/mode/start";
        assertEquals("12", engine2.findValue(path, "id"));
        assertEquals("start", engine2.findValue(path, "mode"));
        assertNull(engine1.findValue(path, "foo"));
    }
    
    @Test
    public void Find_Calendar_Values() {
        String path1 = "/calendar";
        assertNull(calendar.findValue(path1, "month"));
        assertNull(calendar.findValue(path1, "year"));
        String path2 = "/calendar/2011/08";
        assertEquals("2011", calendar.findValue(path2, "year"));
        assertEquals("08", calendar.findValue(path2, "month"));
    }
    
    @Test
    public void Find_Optional_Values() {
        String path1 = "/engine/mode/start";
        assertEquals("start", optional.findValue(path1, "a"));
        assertNull(optional.findValue(path1, "b"));
        String path2 = "/engine/mode/stop";
        assertEquals("stop", optional.findValue(path2, "b"));
        assertNull(optional.findValue(path2, "a"));
    }
    
    @Test
    public void Find_User1_Values() {
        String path = "/user/12";
        assertEquals("12", user1.findValue(path, "id"));
        assertNull(engine1.findValue(path, "foo"));
    }
    
    @Test
    public void Find_User2_Values() {
        String path = "/user/12/(me/something";
        assertEquals("12", user2.findValue(path, "id"));
        assertEquals("something", user2.findValue(path, "foo"));
        assertNull(engine1.findValue(path, "foo"));
    }
    
    @Test(expected=WebApplicationException.class)
    public void Invalid_Path_1() {
        mapping("regex:<id>/mode/<mode(start|stop)>");
    }
    
    @Test(expected=WebApplicationException.class)
    public void Invalid_Path_2() {
        mapping("regex:<id:>/mode");
    }
    
    @Test(expected=WebApplicationException.class)
    public void Invalid_Path_3() {
        mapping("regex:<:>/mode");
    }
    
    @Test(expected=WebApplicationException.class)
    public void Invalid_Path_4() {
        mapping("regex:<:ll>/mode");
    }
    
    @Test(expected=WebApplicationException.class)
    public void Invalid_Path_5() {
        mapping("regex:<>/mode");
    }
    
    @Test(expected=WebApplicationException.class)
    public void Invalid_Path_6() {
        mapping("<>/mode");
    }
    
    @Test(expected=WebApplicationException.class)
    public void Invalid_Path_7() {
        mapping("<rere:>/mode");
    }
    
    @Test(expected=WebApplicationException.class)
    public void Invalid_Path_8() {
        mapping("<rere:fff>/mode");
    }
    
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
        return fact.getMapping(null, new InitServlet(null, null, null), path);
    }

}
