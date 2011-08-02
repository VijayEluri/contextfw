package net.contextfw.web.application.component;

import org.junit.Test;

public class ComponentTest2 extends BaseComponentTest {

    public static class A extends Component {
        
        @SuppressWarnings("unused")
        @Attribute
        private String a = "A";

        @Element
        public String b() {
            return "b";
        }
    }
    
    public static class C extends A {
        
        @SuppressWarnings("unused")
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