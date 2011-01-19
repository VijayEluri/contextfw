package net.contextfw.web.application.internal;

import net.contextfw.web.application.conf.WebConfiguration;

import org.junit.Test;

public class WebResponderTest {

    @Test
    public void testReadingXSL() {
        WebConfiguration configuration = new WebConfiguration();
        configuration.addResourcePaths("net.contextfw.web.application.internal");
        WebResponder responder = new WebResponder(configuration);
        System.out.println(responder.getXSLDocumentContent());
    }
}
