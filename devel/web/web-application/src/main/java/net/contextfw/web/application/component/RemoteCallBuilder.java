package net.contextfw.web.application.component;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.internal.component.ComponentBuilder;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class RemoteCallBuilder {

    private final Gson gson;
    
    private final ComponentBuilder componentBuilder;
    
    @Inject
    public RemoteCallBuilder(Gson gson, ComponentBuilder componentBuilder) {
        this.gson = gson;
        this.componentBuilder = componentBuilder;
    }

    public void buildComponentCall(DOMBuilder b, Component component, String method, Object... args) {
        StringBuilder sb = new StringBuilder(
                componentBuilder.getBuildName(component.getClass()) 
                + "('"+StringEscapeUtils.escapeJavaScript(component.getId())+"')."
                + StringEscapeUtils.escapeJavaScript(method)+"(");
        
        addArguments(sb, args);
        sb.append(");");
        b.descend("Script").text(sb);
    }
    
    public void buildCall(DOMBuilder b, String method, Object... args) {
        StringBuilder sb = new StringBuilder(StringEscapeUtils.escapeJavaScript(method));
        sb.append("(");
        addArguments(sb, args);
        sb.append(");");
        b.descend("Script").text(sb);
    }

    private void addArguments(StringBuilder sb, Object... args) {
        String separator = "";
        if (args != null) {
            for (Object arg : args) {
                if (Boolean.class.isAssignableFrom(arg.getClass()) || Number.class.isAssignableFrom(arg.getClass())) {
                    sb.append(separator + StringEscapeUtils.escapeJavaScript(arg.toString()));
                } else {
                    sb.append(separator + gson.toJson(arg));
                }
                separator = ",";
            }
        }
    }
}