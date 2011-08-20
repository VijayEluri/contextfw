package net.contextfw.application;

import net.contextfw.web.application.WebApplicationModule;
import net.contextfw.web.application.configuration.Configuration;

import org.junit.Test;

import com.google.inject.AbstractModule;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        Configuration conf = Configuration.getDefaults();
        install(new WebApplicationModule(conf));
    }
    
    @Test
    public void nullTest() {}

}
