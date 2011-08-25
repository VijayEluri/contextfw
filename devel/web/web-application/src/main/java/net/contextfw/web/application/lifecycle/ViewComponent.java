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

/**
 * Denotes a view component to be listening the initialization process.
 * 
 * <p>
 *  When a view component implements this interface, system calls the <code>initialize</code>-method
 *  after member injection has been done. The main purpose of this interface is to ask the system
 *  to initialize a child view, if it can be done. The child view can be obtained by the 
 *  {@link ViewContext}
 * </p>
 * 
 */
public interface ViewComponent {
    void initialize(ViewContext context);
}
