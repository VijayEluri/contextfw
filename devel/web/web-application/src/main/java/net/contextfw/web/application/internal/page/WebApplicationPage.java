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

package net.contextfw.web.application.internal.page;

import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.internal.service.WebApplication;

import com.google.inject.Key;

public interface WebApplicationPage extends net.contextfw.web.application.WebApplication {

    <T> T setBean(Key<T> key, T value);
    
    <T> T getBean(Key<T> key);
    
    String getRemoteAddr();
    
    WebApplicationHandle getHandle();
    
    WebApplication getWebApplication();
    
    void setWebApplication(WebApplication application);
    
    int refresh(long expires);
    
    boolean isExpired(long now);
    
    
}
