package net.contextfw.application;

import java.util.Map;
import java.util.WeakHashMap;

import org.junit.Test;

public class MapTest {

    @Test
    public void test() {
        Map<Class<?>, Object> map = new WeakHashMap<Class<?>, Object>();
        
        System.out.println(map.containsKey(String.class));
        map.put(String.class, "ERER");
        System.out.println(map.containsKey(String.class));
        System.out.println(map.containsKey("3434".getClass()));
    }
}
