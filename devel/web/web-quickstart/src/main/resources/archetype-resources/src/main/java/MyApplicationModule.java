#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import net.contextfw.web.application.WebApplicationModule;
import net.contextfw.web.application.lifecycle.DefaultPageFlowFilter;
import net.contextfw.web.application.lifecycle.PageFlowFilter;
import net.contextfw.web.application.properties.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mycila.inject.jsr250.Jsr250;

public class MyApplicationModule extends AbstractModule {

    private WebApplicationModule webApplicationModule;
    
    public MyApplicationModule() {}

    @Override
    protected void configure() {

        Properties props = Properties.getDefaults()
          .add(Properties.RESOURCE_PATH, "${package}")
          .add(Properties.VIEW_COMPONENT_ROOT_PACKAGE, "${package}.views")
          .set(Properties.DEVELOPMENT_MODE, true)
          .set(Properties.XML_PARAM_NAME, "xml")
          .set(Properties.LOG_XML, true);
       
        webApplicationModule = new WebApplicationModule(props);
        
        install(webApplicationModule);
        install(Jsr250.newJsr250Module());
    }
    
    @Provides
    @Singleton
    public PageFlowFilter providePageFlowFilter() {
        return new DefaultPageFlowFilter();
    }
    
    public WebApplicationModule getWebApplicationModule() {
        return webApplicationModule;
    }
}