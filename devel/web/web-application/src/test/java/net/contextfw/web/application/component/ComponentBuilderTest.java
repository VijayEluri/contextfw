package net.contextfw.web.application.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.contextfw.web.application.component.Attribute;
import net.contextfw.web.application.component.Buildable;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.component.CustomBuild;
import net.contextfw.web.application.component.Element;
import net.contextfw.web.application.dom.DOMBuilder;

import org.junit.Test;

public class ComponentBuilderTest extends BaseComponentTest {

    public static class Aa extends Component {

        @Attribute
        public String foo = "bar";
        
        @Element 
        public String bar="foo";
        
        @CustomBuild
        public void custom(DOMBuilder b) {
            b.descend("barFoo").attr("fooBar", true);
        }
    }
    
    public static class Bee extends Component {
        
        @Element
        private Aa comp1;
        
        public Bee(Aa comp1) {
            this.comp1 = this.registerChild(comp1);
        }
        
        @CustomBuild
        public void custom(DOMBuilder b) {
            b.descend("anotherInner").child(comp1);
        }
    }
    
    public static class Cee extends Component {
        @Element
        public Aa aa1;
        @Element
        public Aa aa2;
        @Element
        public Ee ee = new Ee();
        
        @Attribute
        public String nullAttribute = null;
        
        @Element
        public Dee dee = new Dee();
    }
    
    @Buildable
    public static class Dee {
        @Attribute
        public String foo = "bar";
        @Element
        public Aa aa;
    }
    
    public static class Ee {
        public String toString() {
            return "Ee.toString()";
        }
    }
    
    @Test
    public void testId() {
        Aa comp = new Aa();
        assertNull(comp.getId());
        webApplicationComponent.registerChild(comp);
        assertEquals("el1", comp.getId());
        webApplicationComponent.buildChild(domBuilder);
        logXML(domBuilder);
        assertDom("//WebApplication/Aa").hasAttribute("id", "el1");
        assertDom("//WebApplication/Aa/barFoo").hasAttribute("fooBar", "true");
    }
    
    @Test
    public void testInnerComp() {
        Bee comp = new Bee(new Aa());
        webApplicationComponent.registerChild(comp);
        webApplicationComponent.buildChild(domBuilder);
        logXML(domBuilder);
        assertDom("//WebApplication/Bee/comp1/Aa").hasAttribute("id", "el2");
    }
    
    @Test
    public void testEmptyUpdate() {
        Aa comp = new Aa();
        assertNull(comp.getId());
        webApplicationComponent.registerChild(comp);
        webApplicationComponent.buildChildUpdate(domBuilder, componentBuilder);
        logXML(domBuilder);
        assertDom("//WebApplication/Aa").notExists();
    }
    
    @Test
    public void testAaUpdate() {
        Aa comp = new Aa();
        webApplicationComponent.registerChild(comp);
        comp.refresh();
        webApplicationComponent.buildChildUpdate(domBuilder, componentBuilder);
        logXML(domBuilder);
        assertDom("//WebApplication/Aa.update").exists();
    }
    
    @Test
    public void testBeeUpdate() {
        Bee comp = new Bee(new Aa());
        webApplicationComponent.registerChild(comp);
        logXML(domBuilder);
        comp.refresh();
        webApplicationComponent.buildChildUpdate(domBuilder, componentBuilder);
        logXML(domBuilder);
        assertDom("//WebApplication/Bee.update").exists();
        assertDom("//WebApplication/Bee.update/comp1/Aa").exists();
    }
    
    @Test
    public void testBee_Aa_Update() {
        Aa aa = new Aa();
        Bee comp = new Bee(aa);
        webApplicationComponent.registerChild(comp);
        aa.refresh();
        webApplicationComponent.buildChildUpdate(domBuilder, componentBuilder);
        logXML(domBuilder);
        assertDom("//WebApplication/Bee").notExists();
        assertDom("//WebApplication/Bee.update").notExists();
        assertDom("//WebApplication//Aa.update").exists();
    }
    
    @Test 
    public void testCee() {
        Cee cee = new Cee();
        Aa aa = new Aa();
        cee.dee.aa = aa;
        webApplicationComponent.registerChild(cee);
        webApplicationComponent.buildChild(domBuilder);
        logXML(domBuilder);
        assertDom("//WebApplication/Cee/ee/text()").hasText("Ee.toString()");
        assertDom("//WebApplication/Cee").hasNoAttribute("nullAttribute");
        assertDom("//WebApplication/Cee/dee/Dee").exists();
        assertDom("//WebApplication/Cee/dee/Dee/aa/Aa/bar/text()").hasText("foo");
    }
    
    @Test 
    public void testCeeUpdate() {
        Cee cee = new Cee();
        cee.aa1 = cee.registerChild(new Aa());
        cee.aa2 = cee.registerChild(new Aa());
        webApplicationComponent.registerChild(cee);
        cee.aa1.refresh();
        cee.aa2.refresh();
        webApplicationComponent.buildChildUpdate(domBuilder, componentBuilder);
        logXML(domBuilder);
        assertDom("//WebApplication//Aa.update[1]").exists();
        assertDom("//WebApplication//Aa.update[2]").exists();
    }
}