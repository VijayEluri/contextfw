#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.views;

import net.contextfw.web.application.annotations.WebApplicationScoped;
import net.contextfw.web.application.elements.CElement;
import net.contextfw.web.application.elements.enhanced.EmbeddedElement;
import net.contextfw.web.application.elements.enhanced.EnhancedElement;
import net.contextfw.web.application.initializer.Initializer;
import net.contextfw.web.application.initializer.InitializerContext;
import net.contextfw.web.application.initializer.InitializerElement;

@Initializer
@WebApplicationScoped
public class RootView extends EnhancedElement implements InitializerElement {

    @EmbeddedElement
    private CElement child;
    
    @Override
    public void initialize(InitializerContext context) {
        if (context.getChildClass() != null) {
            child = context.initChild();
            registerChild(child);
        }
    }
}