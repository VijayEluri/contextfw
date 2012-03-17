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
import java.util.regex.Pattern;

import net.contextfw.web.application.component.Component;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Partly adapted from Guice 3 to provide identical path matching
 */
public class PathStyleUriMapping extends UriMapping {

    private final String pattern;
    private final Kind patternKind;

    private static enum Kind {
        PREFIX, SUFFIX, LITERAL,
    }

    public PathStyleUriMapping(@NonNull Class<? extends Component> viewClass,
            @NonNull String pattern,
            @NonNull InitServlet initServlet,
            @NonNull Map<String, Pattern> variables) {
        super(viewClass, pattern, initServlet, Type.SERVLET, variables);
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