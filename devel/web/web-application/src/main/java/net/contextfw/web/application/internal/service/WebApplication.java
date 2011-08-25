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

package net.contextfw.web.application.internal.service;

import java.util.List;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.internal.servlet.UriMapping;

import com.google.inject.ImplementedBy;

@ImplementedBy(WebApplicationImpl.class)
public interface WebApplication {

    /**
     * This is called when page is shown for the first time
     */
    void initState(UriMapping mapping);

    /**
     * 
     * @return
     *      true, if web application should be removed
     */
    boolean sendResponse();

    /**
     * This is called when page is updated
     */
    UpdateInvocation updateState(boolean updateComponents, String componentId, String method);
    
    void setInitializerChain(List<Class<? extends Component>> chain);
}