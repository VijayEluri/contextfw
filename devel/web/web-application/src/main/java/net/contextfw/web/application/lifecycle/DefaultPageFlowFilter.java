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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The default implementation for PageFlowFilter
 */
public class DefaultPageFlowFilter implements PageFlowFilter {

    @Override
    public void pageRemoved(int scopeCount, String remoteAddr, String handle) {
    }

    @Override
    public void pageExpired(int scopeCount, String remoteAddr, String handle) {
    }

    @Override
    public String getRemoteAddr(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    @Override
    public void onPageCreate(int scopeCount, String remoteAddr, String handle) {
    }

    @Override
    public void onPageUpdate(int scopeCount, String remoteAddr, String handle, int updateCount) {
    }

    @Override
    public boolean beforePageCreate(int scopeCount, 
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        return true;
    }

    @Override
    public boolean beforePageUpdate(int scopeCount, 
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        return true;
    }
}
