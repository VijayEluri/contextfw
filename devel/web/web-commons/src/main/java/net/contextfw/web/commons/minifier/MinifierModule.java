package net.contextfw.web.commons.minifier;

import static net.contextfw.web.application.configuration.Configuration.DEVELOPMENT_MODE;
import static net.contextfw.web.application.configuration.Configuration.HOST;
import static net.contextfw.web.application.configuration.Configuration.VERSION;
import static net.contextfw.web.commons.minifier.MinifierConf.CSS_FILTER;
import static net.contextfw.web.commons.minifier.MinifierConf.CSS_PATH;
import static net.contextfw.web.commons.minifier.MinifierConf.JS_FILTER;
import static net.contextfw.web.commons.minifier.MinifierConf.JS_PATH;
import net.contextfw.web.application.configuration.Configuration;

import com.google.inject.servlet.ServletModule;

public class MinifierModule extends ServletModule {
    
    private final String jsPath;
    private final String cssPath;
    private final boolean developmentMode;
    private MinifierFilter jsFilter;
    private MinifierFilter cssFilter;
    private final long started;
    private final String host;
    private final String version;
    
    private final MinifierServiceImpl service;
    
    public MinifierModule(Configuration conf) {
        
        this.developmentMode = conf.get(DEVELOPMENT_MODE);
        this.host = conf.get(HOST);
        this.jsPath = conf.get(JS_PATH);
        this.cssPath = conf.get(CSS_PATH);
        this.jsFilter = conf.get(JS_FILTER);
        this.cssFilter = conf.get(CSS_FILTER);
        this.version = conf.get(VERSION);

        service = new MinifierServiceImpl(developmentMode);
        started = System.currentTimeMillis();
    }

    @Override
    protected void configureServlets() {
        bind(MinifierService.class).toInstance(service);
        if (!developmentMode) {
            
            CssMinifierServlet cssMinifier = new CssMinifierServlet(
                    host, cssPath, cssFilter, started, version);
            
            JsMinifierServlet jsMinifier = new JsMinifierServlet(
                    host, jsPath, jsFilter, started, version);
            
            requestInjection(jsMinifier);
            requestInjection(cssMinifier);
            
            service.setJsMinifier(jsMinifier);
            service.setCssMinifier(cssMinifier);
            
            serve(jsMinifier.getMinifiedPath()).with(jsMinifier);
            serve(cssMinifier.getMinifiedPath()).with(cssMinifier);
        }
    }
}
