#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import javax.servlet.ServletContextEvent;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class ServletConfig extends GuiceServletContextListener {

    private MyApplicationModule applicationModule;
    
    @Override
    protected Injector getInjector() {
        applicationModule = new MyApplicationModule();
        return Guice.createInjector(applicationModule);
    }
}
