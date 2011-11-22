package net.contextfw.web.commons.i18n;

import java.util.Locale;
import java.util.Set;

import net.contextfw.web.application.configuration.AddableProperty;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.configuration.SettableProperty;

public class LocaleConf {

    private LocaleConf() {
    }
    
    public static final String NS = "http://www.contextfw.net/i18n";

    public static final String PREFIX = "i18n";
    
    public static final SettableProperty<Locale> DEFAULT_LOCALE = 
            Configuration.createProperty(Locale.class, 
                    LocaleConf.class.getName() + ".defaultLocale");
    
    public static final AddableProperty<Set<Locale>, Locale> SUPPORTED_LOCALE = 
            Configuration.createAddableProperty(Locale.class, 
                    LocaleConf.class.getName() + ".supportedLocale");
            
    public static final SettableProperty<String> BASE_NAME = 
            Configuration.createProperty(String.class, 
                    LocaleConf.class.getName() + ".baseName");
    
    public static final SettableProperty<Boolean> STRICT_VALIDATION = 
            Configuration.createProperty(Boolean.class, 
                    LocaleConf.class.getName() + ".strictValidation");
    
    public static Configuration applyConfiguration(Configuration conf) {
        return conf.add(Configuration.NAMESPACE.as(
                LocaleConf.PREFIX, 
                LocaleConf.NS));
    }
}
