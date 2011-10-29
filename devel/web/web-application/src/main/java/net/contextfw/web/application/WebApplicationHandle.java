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

import java.io.Serializable;

import net.contextfw.web.application.lifecycle.PageScoped;

@PageScoped
public class WebApplicationHandle implements Serializable {

    private static final long serialVersionUID = -2578266439991410555L;

    private final String key;

    public WebApplicationHandle(String key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other instanceof WebApplicationHandle) {
            WebApplicationHandle otherHandle = (WebApplicationHandle) other;
            return this.key.equals(otherHandle.key);
        }
        else {
            return false;
        }
    }
    
    public String toString() {
        return key;
    }
}