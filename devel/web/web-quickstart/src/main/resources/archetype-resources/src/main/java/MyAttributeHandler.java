#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ${package}.util.WebApplicationLocale;
import net.contextfw.web.application.dom.AttributeHandler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class MyAttributeHandler implements AttributeHandler {
    
    private static Map<Locale, String> datePatterns = new HashMap<Locale, String>();
    
    static {
        datePatterns.put(new Locale("fi"), "dd.MM.YYYY");
        datePatterns.put(new Locale("en"), "YYYY/MM/dd");
    }
    
    private Provider<WebApplicationLocale> locale;

    @Inject
    public MyAttributeHandler(Provider<WebApplicationLocale> locale) {
        this.locale = locale;
    }
    
    @Override
    public String toString(Object obj) {
        if (obj != null) {

            if (obj instanceof String) {
                return (String) obj;
            }
//            else if (obj instanceof LocalDate) {
//                return ((LocalDate) obj).toString(datePatterns.get(locale.get().getLocale()));
//            }
//            else if (obj instanceof DateTime) {
//                return ((DateTime) obj).toString("dd.MM.YYYY HH:mm");
//            }
//            else if (obj instanceof LocalTime) {
//                return ((LocalTime) obj).toString("HH:mm");
//            }

            return obj.toString();
        }
        else {
            return "";
        }
    }
}