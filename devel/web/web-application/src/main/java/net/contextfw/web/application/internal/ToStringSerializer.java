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

package net.contextfw.web.application.internal;

import net.contextfw.web.application.serialize.AttributeSerializer;

/**
 * Default implementation for Object serialization.
 * @author marko
 *
 */
public class ToStringSerializer implements AttributeSerializer<Object> {

    @Override
    public String serialize(Object source) {
        return source == null ? null : source.toString();
    }
}
