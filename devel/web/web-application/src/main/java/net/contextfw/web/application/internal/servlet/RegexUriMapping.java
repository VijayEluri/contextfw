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

/**
 * Partly adapted from Guice 3 to provide identical path matching 
 */
public class RegexUriMapping extends UriMapping {

    private final Pattern pattern;
    
    public RegexUriMapping(Class<? extends Component> viewClass, 
                           String path, 
                           InitServlet initServlet, 
                           Map<String, Pattern> variables) {
        super(viewClass, path, initServlet, Type.REGEX, variables);
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
