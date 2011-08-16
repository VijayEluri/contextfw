package net.contextfw.web.application;

import net.contextfw.web.application.configuration.Configuration;

import com.google.inject.AbstractModule;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        Configuration conf = Configuration.getDefaults();
        install(new WebApplicationModule(conf));
    }

}
