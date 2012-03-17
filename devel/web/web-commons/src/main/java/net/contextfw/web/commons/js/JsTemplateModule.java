package net.contextfw.web.commons.js;

import net.contextfw.web.application.configuration.Configuration;

import com.google.inject.servlet.ServletModule;

public class JsTemplateModule extends ServletModule {

    private JsTemplateServlet servlet = new JsTemplateServlet();
    private JsTemplateService service = new JsTemplateServiceImpl(servlet);
    private final String path;
    
    public JsTemplateModule(String templatesPath) {
        this.path = templatesPath;
    }
    
    @Override
    protected void configureServlets() {
        serve(path).with(servlet);
        bind(JsTemplateService.class).toInstance(service);
    }
    
    public Configuration applyConfiguration(Configuration conf) {
        return conf.add(Configuration.NAMESPACE.as(
                JsTemplateService.PREFIX, 
                JsTemplateService.NS));
    }
    
}
