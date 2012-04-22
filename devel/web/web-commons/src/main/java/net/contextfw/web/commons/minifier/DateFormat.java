package net.contextfw.web.commons.minifier;

import java.text.SimpleDateFormat;
import java.util.Locale;

class DateFormat extends ThreadLocal<SimpleDateFormat> {

    protected SimpleDateFormat initialValue() {
        return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
    }
}
