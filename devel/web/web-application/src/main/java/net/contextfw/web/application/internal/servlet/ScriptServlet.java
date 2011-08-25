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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.contextfw.web.application.configuration.Configuration;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ScriptServlet extends ResourceServlet {

    private static final long serialVersionUID = 1L;

    private final List<String> resourcePaths = new ArrayList<String>();

    @Inject
    public ScriptServlet(Configuration configuration) {
        this.resourcePaths.addAll(configuration.get(Configuration.RESOURCE_PATH));
    }
    
    @Override
    public boolean clear() {
        return false;
    }

    @Override
    protected Pattern getAcceptor() {
        return Pattern.compile(".*\\.js", Pattern.CASE_INSENSITIVE);
    }

    @Override
    protected List<String> getRootPaths() {
        return resourcePaths;
    }

    @Override
    protected String getContentType() {
        return "application/javascript";
    }

}