package net.contextfw.web.application.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.lifecycle.AfterBuild;
import net.contextfw.web.application.remote.Remoted;

import org.junit.Test;

public class ClassScannerTest {

    public static class A {
    }
    
    public abstract static class B<T> extends Component {
        @Remoted
        public void b(T t, A a, Long c) {};
    }
    
    public static class C extends B<A> {

    }
    
    @Test
    public void testClassScanning() {
        List<Class<?>> classes = ClassScanner.getClasses(
                "net.contextfw.web.application.internal.util",
                "net.contextfw.web.application.lifecycle");
        
        Set<Class<?>> clsSet = new HashSet<Class<?>>();
        clsSet.addAll(classes);
        
        assertTrue(clsSet.contains(ClassScannerTest.class));
        assertTrue(clsSet.contains(ResourceScannerTest.class));
        assertTrue(clsSet.contains(ClassScanner.class));
        assertTrue(clsSet.contains(ResourceScanner.class));
        assertTrue(clsSet.contains(AfterBuild.class));
    }
    
    @Test
    public void Get_Type_Parameters_For_B() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        B<A> b = new B<A>() {
            @Override
            public void b(A t, A a, Long c) {
                System.out.println("Invoked");
            }
        };
        
        
        Method method = ClassScanner.findMethodForName(b.getClass(), "b");
        assertNotNull("Method", method);
        
        A a = new A();
        
        List<Class<?>> types = ClassScanner.getParamTypes(b.getClass(), method);
        assertEquals(3, types.size());
        assertEquals(A.class, types.get(0));
        assertEquals(A.class, types.get(1));
        assertEquals(Long.class, types.get(2));
        method.invoke(b, new Object[] {a, a, 0L});
    }
    
    @Test
    public void Get_Type_Parameters_For_C() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        
        C c = new C();
        
        Method method = ClassScanner.findMethodForName(c.getClass(), "b");
        assertNotNull("Method", method);
        
        A a = new A();
        
        List<Class<?>> types = ClassScanner.getParamTypes(c.getClass(), method);
        assertEquals(3, types.size());
        assertEquals(A.class, types.get(0));
        assertEquals(A.class, types.get(1));
        assertEquals(Long.class, types.get(2));
        method.invoke(c, new Object[] {a, a, 0L});
    }
}