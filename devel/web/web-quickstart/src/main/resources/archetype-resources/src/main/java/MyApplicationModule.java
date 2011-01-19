#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import net.contextfw.web.application.conf.WebConfiguration;
import net.contextfw.web.application.WebApplicationModule;

import org.guiceyfruit.jsr250.Jsr250Module;

import com.google.inject.AbstractModule;

public class MyApplicationModule extends AbstractModule {

    public MyApplicationModule() {}

    @Override
    protected void configure() {

        WebConfiguration config = new WebConfiguration()
            .addResourcePaths("${package}")
            .setViewComponentRootPackages("${package}.views")
            .debugMode(true);
        
        config.setXmlParamName("xml");
        config.setLogXML(true);
        
        install(new WebApplicationModule(config));
        install(new Jsr250Module());
    }
}