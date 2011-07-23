package net.contextfw.web.application.internal;

import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.internal.service.DirectoryWatcher;

import org.junit.Test;

public class WebResponderTest {

    @Test
    public void testReadingXSL() {
        Configuration configuration = Configuration.getDefaults();
        configuration.add(Configuration.RESOURCE_PATH ,"net.contextfw.web.application.internal");
        WebResponder responder = new WebResponder(configuration, null, 
                new DirectoryWatcher(configuration.get(Configuration.RESOURCE_PATH), null));
        System.out.println(responder.getXSLDocumentContent());
    }
    
    @Test
    public void test2() {
        Pattern pattern = Pattern.compile(".+\\.(xsl|css|js)", Pattern.CASE_INSENSITIVE);
        assertTrue(pattern.matcher("dfd/test.js").matches());
    }
}
