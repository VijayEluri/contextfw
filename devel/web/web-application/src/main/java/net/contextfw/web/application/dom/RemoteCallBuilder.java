package net.contextfw.web.application.dom;

import org.apache.commons.lang.StringEscapeUtils;

public class RemoteCallBuilder {

    public static void buildCall(DOMBuilder b, String ns, String id, String method, Object... args) {
        StringBuilder sb = new StringBuilder(ns + "('"+id+"')."+method+"(");
        String separator = "";
        if (args != null) {
            for (Object arg : args) {
                if (Boolean.class.isAssignableFrom(arg.getClass()) || Number.class.isAssignableFrom(arg.getClass())) {
                    sb.append(separator + StringEscapeUtils.escapeJavaScript(arg.toString()));
                } else {
                    sb.append(separator + "'"+StringEscapeUtils.escapeJavaScript(arg.toString())+"'");
                }
                separator = ",";
            }
        }
        sb.append(");");
        b.descend("Script").text(sb);
    }
}