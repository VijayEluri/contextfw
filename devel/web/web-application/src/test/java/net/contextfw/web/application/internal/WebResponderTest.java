package net.contextfw.web.application.internal;

import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.internal.service.DirectoryWatcher;

import org.junit.Test;

public class WebResponderTest {

    @Test
    public void testReadingXSL() {
        Configuration configuration = Configuration.getDefaults();
        configuration.add(Configuration.RESOURCE_PATH ,"net.contextfw.web.application.internal");
        WebResponder responder = new WebResponder(configuration, null, new DirectoryWatcher(configuration));
        System.out.println(responder.getXSLDocumentContent());
    }
}
