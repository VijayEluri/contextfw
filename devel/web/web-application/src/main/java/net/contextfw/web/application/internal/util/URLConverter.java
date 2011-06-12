package net.contextfw.web.application.internal.util;

import java.util.regex.Pattern;

public class URLConverter {

    /**
     * * -&gt; [^/]+
     * 
     *  /url/ /url/* => /url(/*)
     *  
     *  plain:
     * 
     * @param url
     * @return
     */
    public static String toUrlPattern(String url) {
        return url.startsWith("regex:") ? fromRegex(url) : fromPathLike(url); 
    }
    
    private static String fromRegex(String url) {
        return url.substring(6);
    }
    
    private static String fromPathLike(String url) {
        String[] splits = url.split("\\*");
        StringBuilder b = new StringBuilder();
        
        Pattern.quote(url).replaceAll("\\*", "[^/]*");
        
        return b.toString();
    }
    
}
