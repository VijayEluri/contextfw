package net.contextfw.web.commons.i18n;

import java.util.Locale;
import java.util.Set;

public class LocaleConf {

    public static final String NS = "http://www.contextfw.net/i18n";

    public static final String PREFIX = "i18n";
    
    private final Locale def;

    private final Set<Locale> locales;

    private final String baseName;

    private final boolean strict;

    public LocaleConf(Locale def, Set<Locale> locales, String baseName, boolean strict) {
        super();
        this.def = def;
        this.locales = locales;
        this.baseName = baseName;
        this.strict = strict;
    }
   
    Locale getDef() {
        return def;
    }

    Set<Locale> getLocales() {
        return locales;
    }

    String getBaseName() {
        return baseName;
    }

    boolean isStrict() {
        return strict;
    }
}
