#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import net.contextfw.web.application.ModuleConfiguration;
import net.contextfw.web.application.WebApplicationModule;
import net.contextfw.web.application.WebApplicationServletModule;

import org.guiceyfruit.jsr250.Jsr250Module;

import com.google.inject.AbstractModule;

public class MyApplicationModule extends AbstractModule {

    public MyApplicationModule() {}

    @Override
    protected void configure() {

        ModuleConfiguration config = new ModuleConfiguration()
            .addResourcePaths("${package}")
            .setViewComponentRootPackages("${package}.views")
            .debugMode(true);
        
        config.setXmlParamName("xml");
        config.setLogXML(true);
        
        install(new WebApplicationModule(config));
        install(new Jsr250Module());
        install(new WebApplicationServletModule(config));
    }
}