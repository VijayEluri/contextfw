package net.contextfw.web.application.internal;

import java.util.HashMap;
import java.util.Map;

import net.contextfw.web.application.annotations.WebApplicationScoped;
import net.contextfw.web.application.elements.CElement;

@WebApplicationScoped
public class ElementRegister {

    private int idCounter = 1;

    private Map<String, CElement> elements = new HashMap<String, CElement>();

    private String getNextId() {
        return "el" + idCounter++;
    }

    public void register(CElement element) {
        element.setId(getNextId());
        elements.put(element.getId(), element);
    }

    public void unregister(CElement element) {
        elements.remove(element.getId());
    }

    public CElement findElement(String id) {
        return elements.get(id);
    }
}