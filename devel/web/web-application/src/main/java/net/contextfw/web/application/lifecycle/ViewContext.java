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

package net.contextfw.web.application.lifecycle;

import net.contextfw.web.application.component.Component;

/**
 * Provides an access to child view.
 *
 */
public interface ViewContext {
    
    /**
     * @return
     *   The class of child component or <code>null</code> if there is no child view.
     */
    Class<? extends Component> getChildClass();
    
    /**
     * Initializes a child component if it exists. Initialization does not register the component
     * to the parent and must be made manually. Throws exception if child does not exist.
     * 
     * @return
     *   The child component.
     */
    Component initChild();
}