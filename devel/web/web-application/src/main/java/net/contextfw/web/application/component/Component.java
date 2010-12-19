package net.contextfw.web.application.component;

import java.util.HashSet;
import java.util.Set;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.internal.component.ComponentBuilder;

@Buildable
public class Component {

    private final Set<String> partialUpdates = new HashSet<String>();
    private String partialUpdateName;
    private RefreshMode refreshMode = RefreshMode.NONE;
    private Component parent = null;
    private Set<Component> children = null;
    private Set<Component> waitingToRegister = null;

    private enum RefreshMode {
        NONE, PASS, UPDATE
    };

    @Attribute
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public <T extends Component> T registerChild(T el) {
        if (children == null) {
            children = new HashSet<Component>();
            waitingToRegister = new HashSet<Component>();
        }

        children.add(el);
        el.parent = this;

        if (bubbleRegisterUp(el)) {
            el.registerChildren();
        } else {
            waitingToRegister.add(el);
        }
        return el;
    }

    private void registerChildren() {
        if (waitingToRegister != null) {
            for (Component comp : waitingToRegister) {
                registerChild(comp);
            }
            waitingToRegister.clear();
        }
    }

    protected boolean bubbleRegisterUp(Component el) {
        if (parent != null) {
            return parent.bubbleRegisterUp(el);
        } else {
            return false;
        }

    }

    protected void bubbleUnregisterUp(Component el) {
        if (parent != null) {
            parent.bubbleUnregisterUp(el);
        }
    }

    public void unregisterChild(Component el) {
        if (children != null) {
            children.remove(el);
            bubbleUnregisterUp(el);
        }
    }

    public void refresh() {
        if (id != null) {
            refreshMode = RefreshMode.UPDATE;
            Component p = parent;

            while (p != null) {
                if (p.refreshMode == RefreshMode.NONE) {
                    p.refreshMode = RefreshMode.PASS;
                    p = p.parent;
                } else {
                    p = null;
                }
            }
        }
    }

    public final void buildComponentUpdate(DOMBuilder domBuilder, ComponentBuilder builder) {
        boolean isNormalUpdate = (partialUpdateName == null);

        if (refreshMode == RefreshMode.UPDATE) {
            if (isNormalUpdate) {
                builder.buildUpdate(domBuilder, this, isNormalUpdate ? "update" : partialUpdateName);
            } else {
                builder.buildPartialUpdate(domBuilder, this, isNormalUpdate ? "update" : partialUpdateName, partialUpdates);
            }
        }
        if (refreshMode == RefreshMode.PASS || !isNormalUpdate) {
            if (children != null) {
                for (Component child : children) {
                    child.buildComponentUpdate(domBuilder, builder);
                }
            }
        }
        clearCascadedUpdate();
    }

    public void partialRefresh(String buildName, String... updates) {
        if (partialUpdates != null) {
            this.partialUpdateName = buildName;
            for (String partialUpdate : updates) {
                partialUpdates.add(partialUpdate);
            }
            partialUpdates.add("id");
        }
        refresh();
    }

    public void clearCascadedUpdate() {
        if (refreshMode == RefreshMode.NONE)
            return;
        refreshMode = RefreshMode.NONE;

        if (children != null) {
            for (Component child : children) {
                child.clearCascadedUpdate();
            }
        }

        partialUpdates.clear();
        partialUpdateName = null;
    }
}
