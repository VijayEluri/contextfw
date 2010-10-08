package net.contextfw.web.application.internal.enhanced;

import java.io.StringWriter;

import net.contextfw.application.TstAttributeHandler;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.internal.WebResponder;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnhancedElementTest {

    private Logger logger = LoggerFactory.getLogger(EnhancedElementTest.class);
    
    @Test
    public void element1() {
        EnhancedElementBuilder builder = new EnhancedElementBuilder();
        Element1 element1 = new Element1();
        element1.setBuilder(builder);
        DOMBuilder b = new DOMBuilder("WebApplication", new TstAttributeHandler());
        element1.build(b);
        logXML(b);
    }
    
    public void logXML(DOMBuilder b) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer;

            StringWriter xml = new StringWriter();
            writer = new XMLWriter(xml, format);
            writer.write(b.toDocument());
            System.out.println(xml.toString());
            
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }
}
