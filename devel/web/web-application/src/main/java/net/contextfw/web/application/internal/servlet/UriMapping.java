/**
 * Copyright 2010 Marko Lavikainen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.contextfw.web.application.internal.servlet;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.contextfw.web.application.component.Component;

public abstract class UriMapping implements Comparable<UriMapping> {

    public enum Type {
        SERVLET, REGEX;
    }
    
    private Map<String, Pattern> variables;
    
    private final Type type;
    
    private final String path;
    
    private final Class<? extends Component> viewClass;
    
    private final InitServlet initServlet;
    
    public UriMapping(Class<? extends Component> viewClass, 
                       String path, 
                       InitServlet initServlet, 
                       Type type,
                       Map<String, Pattern> variables) {
        this.initServlet = initServlet;
        this.viewClass = viewClass;
        this.path = path;
        this.type = type;
        this.initServlet.setMapping(this);
        this.variables = variables;
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
        if (!variables.containsKey(name)) {
            return null;
        } else {
            Matcher m = variables.get(name).matcher(path);
            m.find();
            return m.group(1);
        }
    }
    
    public abstract boolean matches(String uri);

    public abstract String extractPath(String pattern);

    public Type getType() {
        return type;
    }
}    
