package net.contextfw.web.application.component;


/**
 * Defines a javascript call invoked from server side.
 * 
 * <p>
 * This class is a convinience class to make calls in with correct convention.
 * </p>
 * <p>
 * The call will be in form: <code>functionName([args,...]);
 * </p>
 */
public class FunctionCall extends Script {

    private final String function;
    private final Object[] args;
    
    public FunctionCall(String function, Object... args) {
        this.function = function;
        this.args = args;
    }

    protected static String toScript(String function, int argCount) {
        StringBuilder b = new StringBuilder(function).append("(");
        String delim = "";
        for (int i = 0; i < argCount; i++) {
            b.append(delim).append("{").append(i).append("}");
            delim = ",";
        }
        b.append(");\n");
        return b.toString();
    }

    @Override
    public String getScript(ScriptContext scriptContext) {
        return FunctionCall.toScript(function,
                args == null ? 0 : args.length);
    }

    @Override
    public Object[] getArguments(ScriptContext scriptContext) {
        return args;
    }
}
