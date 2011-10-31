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

package net.contextfw.web.application.internal.configuration;

/**
 * Defines generic property
 * 
 * @param <T>
 *   Type of the property
 */
public interface Property<T> {
    
    /**
     * Returns the key for this property. 
     * 
     * <p>
     *  All information is stored by their keys thus keys must be unique.
     * </p>
     * @return
     */
    String getKey();
    
    /**
     * Validates the value
     * @param value
     *   The value
     * @return
     *   The value
     */
    T validate(T value);
}
