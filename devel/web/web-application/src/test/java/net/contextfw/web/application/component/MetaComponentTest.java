package net.contextfw.web.application.component;

import static org.junit.Assert.*;
import junit.framework.Assert;
import net.contextfw.web.application.internal.component.ComponentRegister;
import net.contextfw.web.application.internal.component.MetaComponent;

import org.junit.Test;

public class MetaComponentTest {

    private MetaComponent aMeta = new MetaComponent(A.class, null, null, null);
    
    private static class C extends Component {}
    
    private static class B extends Component {}
    
    private static class A extends Component {
        
        @Element
        private Object a1 = null;
        
        @Element(autoRegister=false)
        private Component a2 = new B();
        
        @Element
        private Component a3 = new C();
        
        private Component a4 = null;
        
        @Override
        protected boolean bubbleRegisterUp(Component el) {
            Assert.assertEquals(C.class, el.getClass());
            callCount++;
            return super.bubbleRegisterUp(el);
        }
        
        private int callCount = 0;
    }
    
    @Test
    public void Autoregister_Children() {
        A a = new A();
        aMeta.registerChildren(a);
        ComponentRegister register = new ComponentRegister();
        register.register(a);
        assertEquals(1, a.callCount);
    }
    
}
