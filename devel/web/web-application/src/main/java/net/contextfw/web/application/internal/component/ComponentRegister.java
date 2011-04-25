package net.contextfw.web.application.internal.component;

import java.util.HashMap;
import java.util.Map;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.lifecycle.PageScoped;

@PageScoped
public class ComponentRegister {

    private int idCounter = 0;

    private Map<String, Component> components = new HashMap<String, Component>();

    private String getNextId() {
        return "el" + idCounter++;
    }

    public void register(Component component) {
        if (component.getId() == null) {
            component.setId(getNextId());
            components.put(component.getId(), component);
        }
    }

    public void unregister(Component component) {
        components.remove(component.getId());
        component.setId(null);
    }

    public Component findComponent(String id) {
        return components.get(id);
    }
}