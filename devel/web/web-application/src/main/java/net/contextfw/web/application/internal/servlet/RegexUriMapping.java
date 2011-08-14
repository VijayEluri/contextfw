package net.contextfw.web.application.internal.servlet;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.contextfw.web.application.component.Component;

/**
 * Partly adapted from Guice 3 to provide identical path matching 
 */
public class RegexUriMapping extends UriMapping {

    private final Pattern pattern;
    
    public RegexUriMapping(Class<? extends Component> viewClass, 
                           String path, 
                           InitServlet initServlet, 
                           List<Split> splits) {
        super(viewClass, path, initServlet, splits, Type.REGEX);
        this.pattern = Pattern.compile(path);
    }

    @Override
    public boolean matches(String uri) {
        return null != uri && this.pattern.matcher(uri).matches();
    }

    @Override
    public String extractPath(String path) {
        Matcher matcher = pattern.matcher(path);
        if (matcher.matches() && matcher.groupCount() >= 1) {
          int end = matcher.start(1);
          if (end < path.length()) {
            return path.substring(0, end);
          }
        }
        return null;
    }
}
