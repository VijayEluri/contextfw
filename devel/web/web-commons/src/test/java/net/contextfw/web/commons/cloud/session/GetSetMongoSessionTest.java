package net.contextfw.web.commons.cloud.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class GetSetMongoSessionTest extends AbstractSessionTest {
    
    public static class A {
        
        private String a1;
        
        private long a2;
        
        public String getA1() {
            return a1;
        }
        
        public void setA1(String a1) {
            this.a1 = a1;
        }
        
        public long getA2() {
            return a2;
        }
        
        public void setA2(long a2) {
            this.a2 = a2;
        }
    }
    
    @Test(expected=NoSessionException.class)
    public void Exception_On_Get_When_No_Open_Session() {
        
        CloudSession session = getBasicSession(
                mockHttpContext(
                        mockRequest(null, null),
                        mockResponse(null)),
                mockSessionHolder(null));
        
        session.get(A.class);
        
    }
    
    @Test
    public void Null_On_Get_After_Expiration() {
        CloudSession session = openValidSession(ResponseExpect.ADD_COOKIE_TWICE);
        session.set(new A());
        assertNotNull(session.get(A.class));
        session.expireSession();
        assertNull(session.get(A.class));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void Exception_On_Get_Null_Key() {
        openValidSession().get(null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void Exception_On_Set_Null_Key() {
        openValidSession().set(null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void Exception_On_Unset_Null_Key() {
        openValidSession().unset((String) null);
    }
    
    @Test(expected=NoSessionException.class)
    public void Exception_On_Set_When_No_Open_Session() {
        
        CloudSession session = getBasicSession(
                mockHttpContext(
                        mockRequest(null, null),
                        mockResponse(null)),
                mockSessionHolder(null));
        
        session.set(new A());
    }
    
    @Test
    public void Get_Null_When_Not_Set() {
        CloudSession session = openValidSession();
        assertNull(session.get(A.class));
    }
    
    @Test
    public void Get_Non_Null_When_Set_1() {
        CloudSession session = openValidSession();
        
        A a = new A();
        a.setA1("a");
        a.setA2(1l);
        
        session.set(a);
        
        A testable = session.get(A.class);
        
        assertEquals(a.getA1(), testable.getA1());
        assertEquals(a.getA2(), testable.getA2());
    }
    
    @Test
    public void Get_Non_Null_When_Set_2() {
        CloudSession session = openValidSession();
        
        A a = new A();
        a.setA1("a");
        a.setA2(1l);
        
        session.set("foo", a);
        
        A testable = session.get("foo", A.class);
        
        assertEquals(a.getA1(), testable.getA1());
        assertEquals(a.getA2(), testable.getA2());
    }
    
    @Test
    public void Get_Null_When_Set_Null() {
        CloudSession session = openValidSession();
        
        A a = new A();
        a.setA1("a");
        a.setA2(1l);
        
        session.set("foo", a);
        session.set("foo", null);
        
        assertNull(session.get("foo", A.class));
    }
    
    @Test
    public void Get_Null_When_Unset_1() {
        CloudSession session = openValidSession();
        
        A a = new A();
        a.setA1("a");
        a.setA2(1l);
        
        session.set(a);
        session.unset(A.class);
        
        assertNull(session.get(A.class));
    }
    
    @Test
    public void Get_Null_When_Unset_2() {
        CloudSession session = openValidSession();
        
        A a = new A();
        a.setA1("a");
        a.setA2(1l);
        
        session.set("foo", a);
        session.unset("foo");
        
        assertNull(session.get("foo", A.class));
    }
    
    @Test
    public void Set_Get_Multiple() {
        
        CloudSession session = openValidSession();
        
        A a1 = new A();
        a1.setA1("a1");
        a1.setA2(1l);
        
        A a2 = new A();
        a2.setA1("a2");
        a2.setA2(2l);
        
        session.set("foo", a1);
        session.set(a2);
        
        A t1 = session.get("foo", A.class);
        A t2 = session.get(A.class);
        
        assertEquals(a1.getA1(), t1.getA1());
        assertEquals(a1.getA2(), t1.getA2());
        assertEquals(a2.getA1(), t2.getA1());
        assertEquals(a2.getA2(), t2.getA2());
    }
    
    private CloudSession openValidSession() {
        return openValidSession(ResponseExpect.ADD_COOKIE);
    }
    
    private CloudSession openValidSession(ResponseExpect responseExpect) {

        mockSession();
        
        CloudSession session = getBasicSession(
                mockHttpContext(
                        mockRequest(null, null),
                        mockResponse(responseExpect, null)),
                mockSessionHolder(FOOBAR));
        
        session.openSession(OpenMode.EXISTING);
        
        return session;
    }
}