package net.contextfw.web.application.internal;

import net.contextfw.web.application.internal.service.DirectoryWatcher;
import net.contextfw.web.application.properties.Properties;

import org.junit.Test;

public class WebResponderTest {

    @Test
    public void testReadingXSL() {
        Properties configuration = Properties.getDefaults();
        configuration.add(Properties.RESOURCE_PATH ,"net.contextfw.web.application.internal");
        WebResponder responder = new WebResponder(configuration, null, new DirectoryWatcher(configuration));
        System.out.println(responder.getXSLDocumentContent());
    }
}
