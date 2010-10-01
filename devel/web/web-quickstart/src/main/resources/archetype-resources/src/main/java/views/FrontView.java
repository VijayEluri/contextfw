#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.views;

import net.contextfw.web.application.annotations.WebApplicationScoped;
import net.contextfw.web.application.elements.enhanced.EnhancedElement;
import net.contextfw.web.application.initializer.Initializer;

@Initializer(url="/", parent=RootView.class)
@WebApplicationScoped
public class FrontView extends EnhancedElement {
}