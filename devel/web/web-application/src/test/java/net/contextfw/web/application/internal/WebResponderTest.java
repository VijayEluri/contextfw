package net.contextfw.web.application.internal;

import net.contextfw.web.application.ModuleConfiguration;

import org.junit.Test;

public class WebResponderTest {

    @Test
    public void testReadingXSL() {
        ModuleConfiguration configuration = new ModuleConfiguration();
        configuration.addResourcePaths("net.contextfw.web.application.internal");
        WebResponder responder = new WebResponder(configuration);
        System.out.println(responder.getXSLDocumentContent());
    }
}
