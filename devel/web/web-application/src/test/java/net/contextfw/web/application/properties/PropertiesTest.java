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

package net.contextfw.web.application.properties;

import static net.contextfw.web.application.configuration.Configuration.ATTRIBUTE_JSON_SERIALIZER;
import static net.contextfw.web.application.configuration.Configuration.DEVELOPMENT_MODE;
import static net.contextfw.web.application.configuration.Configuration.LIFECYCLE_LISTENER;
import static net.contextfw.web.application.configuration.Configuration.NAMESPACE;
import static net.contextfw.web.application.configuration.Configuration.PAGEFLOW_FILTER;
import static net.contextfw.web.application.configuration.Configuration.PROPERTY_PROVIDER;
import static net.contextfw.web.application.configuration.Configuration.REMOVAL_SCHEDULE_PERIOD;
import static net.contextfw.web.application.configuration.Configuration.RESOURCE_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import net.contextfw.web.application.configuration.Configuration;

import org.junit.Test;

public class PropertiesTest {

    @Test
    public void test() {
        
        Configuration props = Configuration.getDefaults()
          .add(RESOURCE_PATH, "net.contextfw.web")
          .add(RESOURCE_PATH, "templates.path")
          .add(NAMESPACE.as("foo", "bar"))
          .add(ATTRIBUTE_JSON_SERIALIZER.as(Date.class, DateSerializer.class));
        
        assertTrue(props.get(DEVELOPMENT_MODE));
        
        assertEquals(2, props.get(RESOURCE_PATH).size());
        assertTrue(props.get(RESOURCE_PATH).contains("net.contextfw.web"));
        assertTrue(props.get(RESOURCE_PATH).contains("templates.path"));
        assertNotNull(props.get(PROPERTY_PROVIDER));
        assertNotNull(props.get(LIFECYCLE_LISTENER));
        assertNotNull(props.get(PAGEFLOW_FILTER));
        //assertEquals(((1*60 + 30) * 1000), props.get(ERROR_TIME).longValue());
        //assertEquals((70 * 1000), props.get(POLL_TIME).longValue());
        assertEquals((60000), props.get(REMOVAL_SCHEDULE_PERIOD).longValue());
        
        assertEquals(1, props.get(NAMESPACE).size());
        
        assertEquals("foo", props.get(NAMESPACE).iterator().next().getKey());
        assertEquals("bar", props.get(NAMESPACE).iterator().next().getValue());
        
        assertEquals(1, props.get(ATTRIBUTE_JSON_SERIALIZER).size());
        
        assertEquals(Date.class, props.get(ATTRIBUTE_JSON_SERIALIZER).iterator().next().getKey());
        assertEquals(DateSerializer.class, props.get(ATTRIBUTE_JSON_SERIALIZER).iterator().next().getValue());
    }
}
