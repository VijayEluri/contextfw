package net.contextfw.web.application.internal.servlet;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.contextfw.web.application.component.Component;

public abstract class UriMapping implements Comparable<UriMapping> {

    public enum Type {
        SERVLET, REGEX;
    }
    
    public static class Split {
        
        private final String value;
        
        private final String variableName;
        
        private Pattern pattern;
        
        public Split(String value, String variableName) {
            this.value = value;
            this.variableName = variableName;
        }

        public String getValue() {
            return value;
        }

        public String getVariableName() {
            return variableName;
        }

        public void setPattern(Pattern pattern) {
            this.pattern = pattern;
        }

        public Pattern getPattern() {
            return pattern;
        }
    }
    
    private final Type type;
    
    private final List<Split> splits;
    
    private final String path;
    
    private final Class<? extends Component> viewClass;
    
    private final InitServlet initServlet;
    
    public UriMapping(Class<? extends Component> viewClass, 
                       String path, 
                       InitServlet initServlet, 
                       List<Split> splits,
                       Type type) {
        this.initServlet = initServlet;
        this.viewClass = viewClass;
        this.path = path;
        this.splits = splits;
        this.type = type;
    }
    
    @Override
    public int hashCode() {
        return path.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof UriMapping) {
            UriMapping other = (UriMapping) o;
            if (this.getType() == other.getType()) {
                return this.path.equals(other.path);
            }
        }
        return false;
    }
    
    @Override
    public int compareTo(UriMapping other) {
        if (this.getType() == Type.SERVLET 
                && other.getType() == Type.REGEX) {
            return -1;
        } else if (this.getType() == Type.REGEX 
                && other.getType() == Type.SERVLET) {
            return 1;
        } else {
            String myUri =this.getMatcherUri();
            String otherUri = other.getMatcherUri();
            if (myUri == null && otherUri == null) {
                return other.path.compareTo(this.path);
            } else if (myUri == null) {
                return 1;
            } else if (otherUri == null) {
                return -1;
            } else {
                return otherUri.compareTo(myUri);
            }
        }
    }
    
    private String getMatcherUri() {
        return extractPath(path);
    }

    public String getPath() {
        return path;
    }

    public Class<? extends Component> getViewClass() {
        return viewClass;
    }

    public InitServlet getInitServlet() {
        return initServlet;
    }
    
    public String findValue(String path, String name) {
        String str = path;
        for (Split split : splits) {
            Matcher m = split.pattern.matcher(str);
            if (m.find()) {
                String v = m.group();
                if (name.equals(split.variableName)) {
                    return v;
                } else {
                    str = str.substring(v.length());
                }
            } else {
                return null;
            }
        }
        return null;
    }
    
    public abstract boolean matches(String uri);

    public abstract String extractPath(String pattern);

    public Type getType() {
        return type;
    }
}    
