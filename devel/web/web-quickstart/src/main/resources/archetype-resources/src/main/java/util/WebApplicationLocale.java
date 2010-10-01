#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.util;

import java.util.Locale;

import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.annotations.WebApplicationScoped;

@WebApplicationScoped
public class WebApplicationLocale {

    private Locale locale = null;
    
    private HttpContext httpContext;

    public void setLocale(String langCode) {
        setLocale(new Locale(langCode));
    }
    
    public void setLocale(Locale locale) {
        this.locale = locale;
        httpContext.getRequest().getSession().setAttribute("locale", this.locale);
    }

    public Locale getLocale() {
        if (locale == null) {
            locale = (Locale) httpContext.getRequest().getSession().getAttribute("locale");
            if (locale == null) {
                setLocale(Locale.getDefault());
            }
        }
        return locale;
    }
}