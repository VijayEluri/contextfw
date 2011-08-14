package net.contextfw.web.application.component;

import junit.framework.Assert;

import org.dom4j.Node;

public class DomAssert {

    private final Node node;
    
    private final String msg;
    
    public DomAssert(String msg, Node node) {
        this.node = node;
        this.msg = msg;
    }
    
    public void notExists() {
        Assert.assertNull(msg, node);
    }
    
    public void exists() {
        Assert.assertNotNull(msg + " not exists", node);
    }
    
    public void hasAttribute(String name, String value) {
        exists();
        Assert.assertTrue(msg + ":" + node.getClass().getSimpleName(), node instanceof org.dom4j.Element);
        Assert.assertEquals(msg, value, ((org.dom4j.Element) node).attributeValue(name));
    }
    
    public void attributeStartsWith(String name, String value) {
        exists();
        Assert.assertTrue(msg + ":" + node.getClass().getSimpleName(), node instanceof org.dom4j.Element);
        Assert.assertTrue(msg, ((org.dom4j.Element) node).attributeValue(name).startsWith(value));
    }
    
    public void hasText(String text) {
        exists();
        Assert.assertEquals("isText:" + msg, text, node.getText());
    }
    
    public void hasNoAttribute(String name) {
        exists();
        Assert.assertNull("hasNoAttribute:"+msg,((org.dom4j.Element) node).attributeValue(name));
    }
}
