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

package net.contextfw.web.application.remote;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.scope.Execution;

/**
 * Defines the implementation for custom response to web client.
 * 
 * <p>
 *  It should be noted that resource is served outside of page context
 *  so all needed information must be given before hand to implementin class.
 *  The reason for this behavior is that creating the response may take time
 *  and user interaction would be blocked if resource is compiled in page scope. 
 * </p>
 * 
 * 
 * @see ResourceBody
 *
 */
public interface ResourceResponse {
    Execution serve(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
