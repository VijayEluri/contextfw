package net.contextfw.web.commons.minifier;

import java.util.Calendar;
import java.util.Date;

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
    
    public MinifierModule(MinifierConf conf) {
        
        this.developmentMode = conf.isDevelopmentMode();
        this.host = conf.getHost();
        this.jsPath = conf.getJsPath();
        this.cssPath = conf.getCssPath();
        this.jsFilter = conf.getJsFilter();
        this.cssFilter = conf.getCssFilter();
        Date now = Calendar.getInstance().getTime();
        this.started = now.getTime();
        this.service = new MinifierServiceImpl(developmentMode);
        this.version = conf.getVersion();
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
