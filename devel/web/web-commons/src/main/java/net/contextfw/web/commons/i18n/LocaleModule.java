package net.contextfw.web.commons.i18n;

import net.contextfw.web.application.configuration.Configuration;

import com.google.inject.AbstractModule;

public class LocaleModule extends AbstractModule {

    private final LocaleConf conf;

    public LocaleModule(LocaleConf conf) {
        this.conf = conf;
    }
    
    @Override
    protected void configure() {
        bind(LocaleService.class).toInstance(new LocaleServiceImpl(conf));
    }
    
    public Configuration applyConfiguration(Configuration conf) {
        return conf.add(Configuration.NAMESPACE.as(
                LocaleConf.PREFIX, 
                LocaleConf.NS));
    }
}
