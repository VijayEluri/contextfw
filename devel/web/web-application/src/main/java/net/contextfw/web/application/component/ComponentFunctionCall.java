package net.contextfw.web.application.component;


/**
 * Defines a javascript call invoked from server side.
 * 
 * <p>
 * This class is a convinience class to make component based calls in with
 * correct convention.
 * </p>
 * <p>
 * The call will be in form: <code>ComponentName.methodName([args,...]);
 * </p>
 */
public class ComponentFunctionCall extends Script {

    private final Class<? extends Component> clazz;
    private final String function;
    private final String id;
    private final Object[] args;

    /**
     * Sole constructor
     * 
     * @param component
     *            The component where this is to be invoked
     * @param function
     *            The function name
     * @param args
     *            The arguments.
     */
    public ComponentFunctionCall(Component component, String function, Object... args) {
        this.clazz = component.getClass();
        this.id = component.getId();
        this.args = args;
        this.function = function;
    }

    
    @Override
    public String getScript(ScriptContext scriptContext) {
        return FunctionCall.toScript(scriptContext.getBuildName(clazz)
                + "(\"" + id + "\")."
                + function,
                args == null ? 0 : args.length);
    }
    
    @Override
    public Object[] getArguments(ScriptContext scriptContext) {
        return args;
    }
}
