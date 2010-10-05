package net.contextfw.web.application.elements;

import java.util.HashSet;
import java.util.Set;

import net.contextfw.web.application.dom.DOMBuilder;

public abstract class CElement implements CSimpleElement {

    public enum RefreshMode {
        NONE, PASS, UPDATE
    };

    private String id;
    private String sClass;
    private boolean visible = true;

    private RefreshMode refreshMode = RefreshMode.NONE;
    private CElement parent = null;
    private Set<CElement> children = null;
    private Set<CElement> waitingToRegister = null;

    public CElement() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void doCascadedUpdate(DOMBuilder b) {
        if (refreshMode == RefreshMode.PASS) {
            if (children != null) {
                for (CElement child : children) {
                    child.doCascadedUpdate(b);
                }
            }
        } else if (refreshMode == RefreshMode.UPDATE) {
            buildUpdate(b);
        }
        clearCascadedUpdate();
    }

    public void clearCascadedUpdate() {
        if (refreshMode == RefreshMode.NONE)
            return;
        refreshMode = RefreshMode.NONE;

        if (children != null) {
            for (CElement child : children) {
                child.clearCascadedUpdate();
            }
        }
    }

    public abstract void buildUpdate(DOMBuilder b);

    public <T extends CElement> T registerChild(T el) {
        if (children == null) {
            children = new HashSet<CElement>();
            waitingToRegister = new HashSet<CElement>();
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
            for (CElement el : waitingToRegister) {
                registerChild(el);
            }
            waitingToRegister.clear();
        }
    }

    protected boolean bubbleRegisterUp(CElement el) {
        if (parent != null) {
            return parent.bubbleRegisterUp(el);
        } else {
            return false;
        }

    }

    protected void bubbleUnregisterUp(CElement el) {
        if (parent != null) {
            parent.bubbleUnregisterUp(el);
        }
    }

    public void unregisterChild(CElement el) {
        if (children != null) {
            children.remove(el);
            bubbleUnregisterUp(el);
        }
    }

    public void refresh() {
        if (id != null) {
            refreshMode = RefreshMode.UPDATE;
            CElement p = parent;

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

    public void setSClass(String sClass) {
        this.sClass = sClass;
    }

    public String getSClass() {
        return sClass;
    }

    protected void addCommonAttributes(DOMBuilder b) {
        b.attr("id", getId()).attr("sClass", sClass);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }
}