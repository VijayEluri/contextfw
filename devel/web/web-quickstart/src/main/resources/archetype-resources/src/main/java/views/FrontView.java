#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.views;

import net.contextfw.web.application.annotations.WebApplicationScoped;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.view.View;

@View(url="/", parent=RootView.class)
@WebApplicationScoped
public class FrontView extends Component {
}