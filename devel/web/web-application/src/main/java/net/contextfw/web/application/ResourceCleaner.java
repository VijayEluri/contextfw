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

package net.contextfw.web.application;

import net.contextfw.web.application.internal.WebResponder;
import net.contextfw.web.application.internal.component.ComponentBuilderImpl;
import net.contextfw.web.application.internal.servlet.CSSServlet;
import net.contextfw.web.application.internal.servlet.ScriptServlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Cleans cached resources 
 *
 * <p>
 *  This class can be used to clean cached resources if needed. This is useful if 
 *  part of the resources have been externalized somewhere else than classpath.
 * </p>
 */
@Singleton
public class ResourceCleaner {
    
    @Inject
    private WebResponder webResponder;
    
    @Inject
    private CSSServlet cssServlet;
    
    @Inject
    private ScriptServlet scriptServlet;
    
    @Inject
    private ComponentBuilderImpl builder;
    
    /**
     * Cleans resources
     */
    public void clean() {
        webResponder.clean();
        cssServlet.clean();
        scriptServlet.clean();
        builder.clean();
    }
}
