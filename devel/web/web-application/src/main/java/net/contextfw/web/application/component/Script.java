package net.contextfw.web.application.component;

import java.text.MessageFormat;

import org.apache.commons.lang.StringEscapeUtils;

import net.contextfw.web.application.internal.component.ComponentBuilder;

import com.google.gson.Gson;

/**
 * This class provides the base implementation for calling javascript commands
 * on web client.
 * 
 * <p>
 * <code>Script</code> can be used as a class property or as a return value of a
 * method. The method or class property must be annotated with
 * {@link ScriptElement}-annotation to be recognized.
 * </p>
 * 
 * <p>
 * The script is using {@link MessageFormat} as script format.
 */
public abstract class Script {

    /**
     * Returns the javascript to be executed. Arguments can be injected using
     *   {@link MessageFormat} convention.
     * 
     * @param scriptContext
     *            The context which can be used to get additional data for
     *            script
     */
    public abstract String getScript(ScriptContext scriptContext);
    
    /**
     * Returns the arguments that are injected to javascript.
     * 
     * @param scriptContext
     *            The context which can be used to get additional data for
     *            script
     */
    public abstract Object[] getArguments(ScriptContext scriptContext);

    public void build(DOMBuilder b, Gson gson, ScriptContext scriptContext) {
        
        Object[] arguments = getArguments(scriptContext);
        
        if (arguments == null) {
            b.text(getScript(scriptContext));
        } else {
            MessageFormat format = new MessageFormat(getScript(scriptContext));
            b.text(format.format(getStringParams(gson, arguments)));
        }
    }

    private Object[] getStringParams(Gson gson, Object[] params) {
        Object[] rv = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (Boolean.class.isAssignableFrom(param.getClass())
                    || Number.class.isAssignableFrom(param.getClass())) {
                rv[i] = StringEscapeUtils.escapeJavaScript(param.toString());
            } else {
                rv[i] = gson.toJson(param);
            }
        }
        return rv;
    }
}
