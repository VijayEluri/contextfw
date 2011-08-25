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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import net.contextfw.web.application.WebApplicationException;

import org.dom4j.Document;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;

public class Transformers {

    private static final TransformerFactory FACTORY =
            TransformerFactory.newInstance();

    private final int count;

    private int current = 0;

    private boolean initialized = false;

    private List<Transformer> transformers;

    public Transformers(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("At least 1 transformer must be declared");
        }
        this.count = count;
    }

    public synchronized void initialize(String content) {
        current = 0;
        transformers = new ArrayList<Transformer>(count);
        for (int i = 0; i < count; i++) {
            try {
                transformers.add(FACTORY.newTransformer(new StreamSource(
                        new StringReader(content))));
            } catch (TransformerConfigurationException e) {
                throw new WebApplicationException("Could not get transformer",
                        e);
            }
        }
        initialized = true;
    }

    public void invalidate() {
        initialized = false;
        transformers = null;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Document transform(Document document) {
        if (initialized) {
            DocumentSource source = new DocumentSource(document);
            DocumentResult result = new DocumentResult();
            Transformer tr = getTransformer();
            synchronized (tr) {
                String lang = document.getRootElement()
                        .attributeValue("xml:lang");
                if (lang != null) {
                    tr.setParameter("xml:lang", lang);
                }
                try {
                    tr.transform(source, result);
                } catch (TransformerException e) {
                    throw new WebApplicationException(e);
                }
            }
            return result.getDocument();
        } else {
            throw new WebApplicationException("Transformers are not initialized");
        }
    }

    private Transformer getTransformer() {
        int i = current;
        current = (current + 1) % count;
        return transformers.get(i);
    }
}
