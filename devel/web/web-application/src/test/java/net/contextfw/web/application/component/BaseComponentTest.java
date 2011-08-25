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

package net.contextfw.web.application.component;

import java.io.StringWriter;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.internal.ToStringSerializer;
import net.contextfw.web.application.internal.component.ComponentBuilder;
import net.contextfw.web.application.internal.component.ComponentBuilderImpl;
import net.contextfw.web.application.internal.component.ComponentRegister;
import net.contextfw.web.application.internal.component.WebApplicationComponent;
import net.contextfw.web.application.serialize.AttributeSerializer;

import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public abstract class BaseComponentTest {
    
    protected Logger log = LoggerFactory.getLogger(BaseComponentTest.class); 
    
    protected ComponentRegister componentRegister;
    
    protected ComponentBuilder componentBuilder;
    
    protected ScriptContext scriptContext;
    
    protected WebApplicationComponent webApplicationComponent;
    
    protected DOMBuilder domBuilder;
    
    protected AttributeSerializer<Object> serializer = new ToStringSerializer();
    
    protected DomAssert assertDom(String xpath) {
        Node node = domBuilder.toDocument().getRootElement().selectSingleNode(xpath);
        return new DomAssert(xpath, node);
    }
    
    @Before
    public void before() {
        componentRegister = new ComponentRegister();
        Gson gson = new Gson();
        componentBuilder = new ComponentBuilderImpl(null, gson);
        scriptContext = (ScriptContext) componentBuilder;
        domBuilder = new DOMBuilder("WebApplication", serializer, componentBuilder);
        webApplicationComponent = new WebApplicationComponent(componentRegister);
    }
    
    public void logXML(DOMBuilder b) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer;

            StringWriter xml = new StringWriter();
            writer = new XMLWriter(xml, format);
            writer.write(b.toDocument());
            log.info("Logged xml:\n"+xml.toString());
            
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }
}
