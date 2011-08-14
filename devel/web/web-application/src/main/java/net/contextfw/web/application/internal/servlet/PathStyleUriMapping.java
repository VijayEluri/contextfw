package net.contextfw.web.application.internal.servlet;

import java.util.List;

import net.contextfw.web.application.component.Component;

/**
 * Partly adapted from Guice 3 to provide identical path matching
 */
public class PathStyleUriMapping extends UriMapping {

    private final String pattern;
    private final Kind patternKind;

    private static enum Kind {
        PREFIX, SUFFIX, LITERAL,
    }

    public PathStyleUriMapping(Class<? extends Component> viewClass,
            String pattern,
            InitServlet initServlet,
            List<Split> splits) {
        super(viewClass, pattern, initServlet, splits, Type.SERVLET);
        if (pattern.startsWith("*")) {
            this.pattern = pattern.substring(1);
            this.patternKind = Kind.PREFIX;
        } else if (pattern.endsWith("*")) {
            this.pattern = pattern.substring(0, pattern.length() - 1);
            this.patternKind = Kind.SUFFIX;
        } else {
            this.pattern = pattern;
            this.patternKind = Kind.LITERAL;
        }
    }

    @Override
    public boolean matches(String uri) {
        if (null == uri) {
            return false;
        }

        if (patternKind == Kind.PREFIX) {
            return uri.endsWith(pattern);
        } else if (patternKind == Kind.SUFFIX) {
            return uri.startsWith(pattern);
        }
        return pattern.equals(uri);
    }

    @Override
    public String extractPath(String pattern) {
        if (patternKind == Kind.PREFIX) {
            return null;
        } else if (patternKind == Kind.SUFFIX) {
            String extract = pattern;
            if (extract.endsWith("/")) {
                extract = extract.substring(0, extract.length() - 1);
            }

            return extract;
        }
        return getPath();
    }
}