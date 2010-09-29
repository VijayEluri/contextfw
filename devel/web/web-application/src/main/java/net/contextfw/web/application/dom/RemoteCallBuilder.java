package net.contextfw.web.application.dom;


public class RemoteCallBuilder {

    public static void buildCall(DOMBuilder superBuilder, String ns, String id, String method, Object... args) {
        DOMBuilder scriptBuilder = superBuilder.descend("Script").descend("JavascriptCall");
        scriptBuilder.attr("ns", ns).attr("id", id).attr("method", method);
        if (args != null) {
            for (Object arg : args) {
                DOMBuilder argBuilder = scriptBuilder.descend("arg");
                if (arg == null) {
                    argBuilder.descend("Null");
                }
                else
                if (Number.class.isAssignableFrom(arg.getClass())) {
                    argBuilder.descend("Number").text(arg);
                }
                else {
                    argBuilder.descend("String").text(arg);
                }
            }
        }
    }
}