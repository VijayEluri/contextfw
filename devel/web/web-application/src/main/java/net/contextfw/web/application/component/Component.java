package net.contextfw.web.application.component;

import java.util.HashSet;
import java.util.Set;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.internal.component.ComponentBuilder;

/**
 * The base class of a component
 *
 */
@Buildable
public abstract class Component {

    private final Set<String> partialUpdates = new HashSet<String>();
    private String partialUpdateName;
    private RefreshMode refreshMode = RefreshMode.NONE;
    private Component parent = null;
    private Set<Component> children = null;
    private Set<Component> waitingToRegister = null;
    
    private boolean enabled = true;

    private enum RefreshMode {
        NONE, PASS, UPDATE
    };

    @Attribute
    private String id;

    /**
     * Assigns an id for the component
     * 
     * <p>
     *  Id is generated automatically by the web framework so setting an id is not necesssary.
     *  the id is of a form <code>el[n]</code> where <code>[n]</code> is an incrementing 
     *  value.  
     * </p>
     * <p>
     *  It is also possible to set a custom id, but to be sure that setting succceeds it must be don
     *  during initialization.
     * </p>
     */
    public void setId(String id) {
        if (this.id != null) {
            throw new WebApplicationException("Component id can be set only once");
        }
        this.id = id;
    }

    /**
     * Get's component id
     * 
     * <p>
     *  The <code>id</code> is <code>null</code> initially, but when component is registered to thes
     *  system id is generated automatically. This means that <code>id</code> may not be in use
     *  at initialization phase and developer should not rely on using <code>id</code> in class properties.
     * </p>
     */
    public String getId() {
        return id;
    }

    /**
     * Adds a component to be a child of this component.
     * 
     * <p>
     *  Registering child components is a mandatory task so that the framework is able to 
     *  register all components in the page and assign proper ids for them.
     * </p>
     * 
     * <p>
     *  Component registering is lazy. If parent component has not yet been registered then
     *  registering for child components is delayd untit parent is also registered.
     * </p>
     * 
     * @param <T>
     *   Component type
     * @param el
     *   The Component
     * @return
     *   The added component
     */
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

    /**
     * For internal use only
     */
    protected boolean bubbleRegisterUp(Component el) {
        if (parent != null) {
            return parent.bubbleRegisterUp(el);
        } else {
            return false;
        }

    }

    /**
     * For internal use only
     */
    protected void bubbleUnregisterUp(Component el) {
        if (parent != null) {
            parent.bubbleUnregisterUp(el);
        }
    }

    /**
     * Removes child component from the framework
     */
    public void unregisterChild(Component el) {
        if (children != null) {
            children.remove(el);
            bubbleUnregisterUp(el);
        }
    }

    /**
     * Refreshes component state to web client
     * 
     * <p>
     *  When component needs to update its state on web client, this
     *  method must be called. Framework recognizes the request and 
     *  creates property update during rendering phase.
     * </p>
     * 
     * <p>
     *  <b>Note!</b> If paren component also requests update, then the update
     *  of this component is canceled, because in normal circumstances this
     *  component if fully redrawn by the parent component.
     * </p>
     */
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

    /**
     * For internal use only
     */
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

    /**
     * Requests a partial update for component
     * 
     * <p>
     *  When there is a need to only partially update component, then this method is used
     * </p>
     * 
     * 
     * @param buildName
     * @param updates
     */
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

    /**
     * For internal use only
     */
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

    /**
     * Defines if this component is enabled or disabled.
     * 
     * <p>
     *  If component is disabled then it is not added to DOM-tree during rendering phase. Also
     *  disabled component does not listen remote calls.
     * </p>
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
