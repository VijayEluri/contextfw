package net.contextfw.web.application.internal.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.contextfw.web.application.PropertyProvider;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.internal.initializer.InitializerProvider;
import net.contextfw.web.application.internal.service.InitHandler;
import net.contextfw.web.application.lifecycle.RequestInvocationFilter;
import net.contextfw.web.application.lifecycle.View;

import org.apache.commons.lang.StringUtils;

public class UriMappingFactory {

    public static class Split {

        private final String value;

        private final String variableName;

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
    }

    private static final String SPLITTER_STR = "\\<[^<>]+>|[^<>]+";
    private static final Pattern PATH_VERIFIER = Pattern.compile("(" + SPLITTER_STR + ")+");
    private static final Pattern REGEX_VARIABLE_VERIFIER = Pattern.compile("^<\\w+(:.+)?>$");
    private static final Pattern PATH_VARIABLE_VERIFIER = Pattern.compile("^<\\w+>$");
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

    private String toEscapedRegex(String path) {
        return path.replaceAll("\\(", "\\\\(")
            .replaceAll("\\)", "\\\\)")
            .replaceAll("\\{", "\\\\{")
            .replaceAll("\\}", "\\\\}")
            .replaceAll("\\[", "\\\\[")
            .replaceAll("\\]", "\\\\]");
    }
    
    private UriMapping getPathStyleMapping(
            Class<? extends Component> viewClass,
            InitServlet initServlet,
            String path) {
        List<Split> splits = splitByVariables(toEscapedRegex(path), PATH_VARIABLE_VERIFIER);
        StringBuilder constructedPath = new StringBuilder();
        for (Split split : splits) {
            if (split.getVariableName() != null) {
                constructedPath.append("*");
            } else {
                constructedPath.append(split.getValue());
            }
        }
        return new PathStyleUriMapping(viewClass,
                               constructedPath.toString(),
                               initServlet,
                               getRegexVariables(splits));
    }

    private Map<String, Pattern> getRegexVariables(List<Split> splits) {
        Map<String, Pattern> variables = new HashMap<String, Pattern>();
        for (int i = 0; i < splits.size(); i++) {
            if (splits.get(i).getVariableName() != null) {
                variables.put(splits.get(i).getVariableName(),
                        getRegexVariableMatcher(splits, i));
            }
        }
        for (Split split : splits) {
            if (split.getVariableName() != null) {

            }
        }
        return variables;
    }

    private Pattern getRegexVariableMatcher(List<Split> splits, int pos) {
        StringBuilder before = new StringBuilder("");
        StringBuilder after = new StringBuilder("");
        for (int i = 0; i < pos; i++) {
            before.append(splits.get(i).getValue());
        }
        for (int i = pos + 1; i < splits.size(); i++) {
            after.append(splits.get(i).getValue());
        }

        return Pattern.compile(
                toNonCapturingMode(before.toString()) +
                        "(" + splits.get(pos).getValue() + ")" +
                        toNonCapturingMode(after.toString()));
    }

    private String toNonCapturingMode(String part) {
        StringBuilder sb = new StringBuilder();
        String[] splits = part.split("\\\\\\(");
        String delim = "";
        for (String split : splits) {
            sb.append(delim).append(split.replaceAll("\\(", "(?:"));
            delim = "\\(";
        }
        return sb.toString();
    }

    private UriMapping getRegexMapping(
            Class<? extends Component> viewClass,
            InitServlet initServlet,
            String path) {
        List<Split> splits = splitByVariables(path, REGEX_VARIABLE_VERIFIER);

        StringBuilder constructedPath = new StringBuilder();
        for (Split split : splits) {
            constructedPath.append(split.getValue());
        }
        return new RegexUriMapping(viewClass,
                               constructedPath.toString(),
                               initServlet,
                               getRegexVariables(splits));
    }

    private List<Split> splitByVariables(String path, Pattern variableVerifier) {
        if (!PATH_VERIFIER.matcher(path).matches()) {
            throw new WebApplicationException("Path '" + path + "' is not valid");
        }

        List<Split> splits = new ArrayList<Split>();

        Matcher matcher = PATH_SPLITTER.matcher(path);
        while (matcher.find()) {
            String group = matcher.group();
            if (group.startsWith("<")) {
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
                    + split + "' in path '" + path + "' is not valid.");
        }
        String def = split.substring(1, split.length() - 1);
        String name = StringUtils.substringBefore(def, ":");
        String value = StringUtils.substringAfter(def, ":");
        if (StringUtils.isBlank(value)) {
            value = "[^/]+";
        }

        return new Split(value, name);
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
                            mappings.add(this.getMapping((Class<? extends Component>) cl, servlet,
                                    url));
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
                                mappings.add(this.getMapping((Class<? extends Component>) cl,
                                        servlet, url));
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