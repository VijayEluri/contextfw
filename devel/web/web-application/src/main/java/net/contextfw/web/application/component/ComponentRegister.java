package net.contextfw.web.application.component;

public interface ComponentRegister {

    <C extends Component> C findComponent(Class<C> cl, String id);
}
