package net.contextfw.web.application.internal.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.contextfw.web.application.PropertyProvider;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.internal.initializer.InitializerProvider;
import net.contextfw.web.application.internal.service.InitHandler;
import net.contextfw.web.application.internal.servlet.UriMapping.Split;
import net.contextfw.web.application.lifecycle.RequestInvocationFilter;
import net.contextfw.web.application.lifecycle.View;

import org.apache.commons.lang.StringUtils;

public class UriMappingFactory {

    private static final String SPLITTER_STR = "\\{[^\\{\\}]+\\}|[^\\{\\}]+";
    private static final Pattern PATH_VERIFIER = Pattern.compile("(" + SPLITTER_STR + ")+");
    private static final Pattern REGEX_VARIABLE_VERIFIER = Pattern.compile("^\\{\\w+(:.+)?\\}$");
    private static final Pattern PATH_VARIABLE_VERIFIER = Pattern.compile("^\\{\\w+\\}$");
    private static final Pattern PATH_SPLITTER = Pattern.compile(SPLITTER_STR);
    
    private static final String REGEX = "regex:";
    
    public UriMapping getMapping(Class<? extends Component> viewClass,
                                 InitServlet initServlet,
                                 String path) {
        if (path.startsWith(REGEX)) {
            return getRegexMapping(viewClass, initServlet,
                    path.substring(REGEX.length()));
        } else {
            return getPathStyleMapping(viewClass, initServlet, path);
        }
    }
    
    private UriMapping getPathStyleMapping(
            Class<? extends Component> viewClass,
            InitServlet initServlet,
            String path) {
        List<Split> splits = splitByVariables(path, PATH_VARIABLE_VERIFIER);
        StringBuilder constructedPath = new StringBuilder();
        for (Split split : splits) {
            Pattern p;
            if (split.getVariableName() != null) {
                constructedPath.append("*");
                p = Pattern.compile("[^/]+");
            } else {
                constructedPath.append(split.getValue());
                p = Pattern.compile(split.getValue());
            }
            split.setPattern(p);
        }
        return new PathStyleUriMapping(viewClass, 
                               constructedPath.toString(), 
                               initServlet,
                               splits);
    }
    
    private UriMapping getRegexMapping(
            Class<? extends Component> viewClass,
            InitServlet initServlet,
            String path) {
        List<Split> splits = splitByVariables(path, REGEX_VARIABLE_VERIFIER);
        StringBuilder constructedPath = new StringBuilder();
        for (Split split : splits) {
            Pattern p;
            if (split.getVariableName() != null) {
                if (split.getValue().contains(":")) {
                    String str = StringUtils.substringAfter(split.getValue(), ":");
                    constructedPath.append(str);
                    p = Pattern.compile(str);
                } else {
                    constructedPath.append("[^/]+");
                    p = Pattern.compile("[^/]+");
                }
            } else {
                constructedPath.append(split.getValue());
                p = Pattern.compile(split.getValue());
            }
            split.setPattern(p);
        }
        return new RegexUriMapping(viewClass, 
                               constructedPath.toString(), 
                               initServlet,
                               splits);
    }
    private List<Split> splitByVariables(String path, Pattern variableVerifier) {
        if (!PATH_VERIFIER.matcher(path).matches()) {
            throw new WebApplicationException("Path '"+path+"' is not valid");
        }
        
        List<Split> splits = new ArrayList<Split>();
        
        Matcher matcher = PATH_SPLITTER.matcher(path);
        while(matcher.find()) {
            String group = matcher.group();
            if (group.startsWith("{")) {
                splits.add(getVariableSplit(path, group, variableVerifier));                
            } else {
                splits.add(new Split(group, null));
            }
        }
        return splits;
    }
    
    private Split getVariableSplit(String path, String split, Pattern verifier) {
        if (!verifier.matcher(split).matches()) {
            throw new WebApplicationException("Variable '"
                    +split+"' in path '"+path+"' is not valid.");
        }
        String value = split.substring(1, split.length() - 1);
        return new Split(value, StringUtils.substringBefore(value, ":"));
    }
    
    @SuppressWarnings("unchecked")
    public SortedSet<UriMapping> createMappings(
                    Collection<Class<?>> origClasses,
                    ClassLoader classLoader,
                    InitializerProvider initializerProvider,
                    InitHandler initHandler, 
                    PropertyProvider properties,
                    RequestInvocationFilter filter) {
            
            // Note: This process creates some phantom chains from 
            // views that do not have any url. Those chains are
            // however ingnored and are not such problem.
            
            SortedSet<UriMapping> mappings = new TreeSet<UriMapping>();
            
            try {
                for (Class<?> origClass : origClasses) {
                    Class<?> cl = classLoader.loadClass(origClass.getCanonicalName());
                    View annotation = cl.getAnnotation(View.class);
                    if (annotation != null) {
                        
                        if (!Component.class.isAssignableFrom(cl)) {
                            throw new WebApplicationException("Class " + cl.getName()
                                    + " annotated with @View does " +
                                    "not extend Component");
                        }
                        
                        List<Class<? extends Component>> chain = 
                            initializerProvider.getInitializerChain(
                                    cl.asSubclass(Component.class));
                        
                        InitServlet servlet = new InitServlet(initHandler, chain, filter);
                        
                        for (String url : annotation.url()) {
                            if (!"".equals(url)) {
                                mappings.add(this.getMapping((Class<? extends Component>) cl, servlet, url));
                            }
                        }
                        
                        for (String property : annotation.property()) {
                            if (!"".equals(property)) {
                                if (!properties.get().containsKey(property)) {
                                    throw new WebApplicationException("No url bound to property: "
                                            + property);
                                }
                                
                                String url = properties.get().getProperty(property);
    
                                if (url != null && !"".equals(url)) {
                                    mappings.add(this.getMapping((Class<? extends Component>) cl, servlet, url));
                                } else {
                                    throw new WebApplicationException(
                                            "No url bound to view component. (class="
                                                    + cl.getSimpleName() + ", property=" + property
                                                    + ")");
                                }
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new WebApplicationException(e);
            }
            
            return mappings;
        }
}