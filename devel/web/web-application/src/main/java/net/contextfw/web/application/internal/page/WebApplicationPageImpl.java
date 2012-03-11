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

import java.util.HashMap;
import java.util.Map;

import net.contextfw.web.application.PageHandle;
import net.contextfw.web.application.internal.service.WebApplication;

import com.google.inject.Key;

public class WebApplicationPageImpl implements WebApplicationPage {

    private static final Key<PageHandle> HANDLE_KEY = Key.get(PageHandle.class);

    private Map<Key<?>, Object> beans = new HashMap<Key<?>, Object>();

    private WebApplication webApplication;
    
    public WebApplicationPageImpl() {
    }
    
    @Override
    public <T> T setBean(Key<T> key, T value) {
        beans.put(key, value);
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Key<T> key) {
        return (T) beans.get(key);
    }

    @Override
    public PageHandle getHandle() {
        return (PageHandle) beans.get(HANDLE_KEY);
    }

    @Override
    public WebApplication getWebApplication() {
        return webApplication;
    }

    @Override
    public void setWebApplication(WebApplication application) {
        this.webApplication = application;
    }

    @Override
    public void setHandle(PageHandle handle) {
        beans.put(HANDLE_KEY, handle);
    }
}