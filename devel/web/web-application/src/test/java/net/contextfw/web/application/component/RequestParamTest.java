package net.contextfw.web.application.component;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import net.contextfw.web.application.internal.component.MetaComponent;
import net.contextfw.web.application.remote.ErrorResolution;
import net.contextfw.web.application.remote.RequestParam;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RequestParamTest {

    private MetaComponent aMeta = new MetaComponent(A.class, null, null, null);
    
    private A a;
    
    private static class A extends Component {

        @RequestParam
        private boolean a;
        
        @RequestParam(name="bee")
        private String b;
        
        @RequestParam(onNull=ErrorResolution.RETHROW_CAUSE)
        private Long d;
        
        private Integer c;
        
        private Double e;
        
        @RequestParam
        private Double f;
        
        @SuppressWarnings("unused")
        @RequestParam(onNull=ErrorResolution.RETHROW_CAUSE)
        public void c(Integer time) {
            this.c = time;
        }
        
        @SuppressWarnings("unused")
        @RequestParam
        public void e(Double e) {
            this.e = e;
        }
    }
    
    @Before
    public void setup() {
        aMeta = new MetaComponent(A.class, null, null, null);
        a = new A();
    }
    
    @Test
    public void Test1() {
        
        HttpServletRequest request = createNiceMock(HttpServletRequest.class);
        expect(request.getParameter("a")).andReturn("true");
        expect(request.getParameter("bee")).andReturn("something");
        expect(request.getParameter("d")).andReturn("123");
        expect(request.getParameter("c")).andReturn("3");
        replay(request);
        
        aMeta.applyRequestParams(a, request);
        assertEquals(true, a.a);
        assertEquals("something", a.b);
        assertEquals((Long) 123L, (Long) a.d);
        assertEquals((Integer) 3, a.c);
        Assert.assertNull(a.e);
        Assert.assertNull(a.f);
    }
    
//    @Test(expected=WebApplicationException.class)
//    public void Test_Null_D() {
//        HttpServletRequest request = createNiceMock(HttpServletRequest.class);
//        aMeta.applyPathParams(a, map2, "/foo/true/something/");
//    }
//    
//    @Test(expected=WebApplicationException.class)
//    public void Test_Null_C() {
//        aMeta.applyPathParams(a, map1, "/foo/true/something/long/123/date/");
//    }
}
