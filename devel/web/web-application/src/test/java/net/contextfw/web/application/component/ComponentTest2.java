package net.contextfw.web.application.component;

import static org.junit.Assert.assertNull;
import net.contextfw.web.application.component.ComponentBuilderTest.Aa;
import net.contextfw.web.application.component.ComponentBuilderTest.Bee;
import net.contextfw.web.application.component.ComponentBuilderTest.Cee;

import org.junit.Test;

public class ComponentTest2 extends BaseComponentTest {

    public static class A extends Component {
        
        @Attribute
        private String a = "A";

        @Element
        public String b() {
            return "b";
        }
    }
    
    public static class C extends A {
        
        @Attribute
        private String a = "C";
        
        @Override
        @Element
        public String b() {
            return "c";
        }
        
    }
    
    
    @Test
    public void testInnerComp() {
        C c = new C();
        webApplicationComponent.registerChild(c);
        webApplicationComponent.buildChild(domBuilder);
        logXML(domBuilder);
        assertDom("//WebApplication/C").hasAttribute("a", "C");
        assertDom("//WebApplication/C/b[1]").exists();
        assertDom("//WebApplication/C/b[2]").notExists();
    }
}