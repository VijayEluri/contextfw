#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.providers;

import ${package}.util.WebApplicationLocale;

import com.google.inject.Provider;

public class WebApplicationLocaleProvider implements Provider<WebApplicationLocale> {

    @Override
    public WebApplicationLocale get() {
        return new WebApplicationLocale();
    }
}
