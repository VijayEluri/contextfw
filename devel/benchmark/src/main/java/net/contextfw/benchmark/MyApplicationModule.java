package net.contextfw.benchmark;

import net.contextfw.web.application.WebApplicationModule;
import net.contextfw.web.application.configuration.Configuration;

import com.google.inject.AbstractModule;
import com.mycila.inject.jsr250.Jsr250;

public class MyApplicationModule extends AbstractModule {

    @Override
    protected void configure() {

        Configuration props = Configuration.getDefaults()
          .add(Configuration.RESOURCE_PATH, "net.contextfw.benchmark")
          .add(Configuration.VIEW_COMPONENT_ROOT_PACKAGE, "net.contextfw.benchmark.views")
          .set(Configuration.DEVELOPMENT_MODE, true)
          .set(Configuration.XML_PARAM_NAME, "xml")
          .set(Configuration.MAX_INACTIVITY.inHoursAndMins(1, 0))
          .set(Configuration.LIFECYCLE_LISTENER.as(MyLifecycleListener.class))
          .set(Configuration.LOG_XML, false);
       
        install( new WebApplicationModule(props));
        install(Jsr250.newJsr250Module());
    }    
}