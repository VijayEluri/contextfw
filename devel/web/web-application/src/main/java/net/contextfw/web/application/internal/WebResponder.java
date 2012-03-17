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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.org.dom4j.io.XMLWriter;
import net.contextfw.web.application.DocumentProcessor;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.development.XMLResponseLogger;
import net.contextfw.web.application.internal.configuration.KeyValue;
import net.contextfw.web.application.internal.util.ResourceEntry;
import net.contextfw.web.application.internal.util.ResourceScanner;
import net.contextfw.web.application.internal.util.Utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class WebResponder {

    private static final Pattern XSL_ACCEPTOR = Pattern.compile(".*\\.xsl",
            Pattern.CASE_INSENSITIVE);

    private Logger logger = LoggerFactory.getLogger(WebResponder.class);

    private List<String> rootResourcePaths = new ArrayList<String>();
    private final List<String> resourcePaths = new ArrayList<String>();
    private final List<KeyValue<String, String>> namespaces =
            new ArrayList<KeyValue<String, String>>();

    private final Transformers transformers;

    private final XMLResponseLogger responseLogger;
    private final DocumentProcessor xslPostProcessor;
    
    private final OutputFormat htmlFormat;

    public enum Mode {

        INIT("text/html;charset=UTF-8"),
        UPDATE("text/xml;charset=UTF-8"),
        XML("text/xml;charset=UTF-8");

        private final String contentType;

        private Mode(String contentType) {
            this.contentType = contentType;
        }

        public String getContentType() {
            return contentType;
        }
    }

    @SuppressWarnings("unchecked")
    @Inject
    public WebResponder(Configuration configuration, Injector injector) {
        rootResourcePaths.add("net.contextfw.web.application");
        transformers = new Transformers();
        resourcePaths.addAll(configuration.get(Configuration.RESOURCE_PATH));
        namespaces.addAll(configuration.get(Configuration.NAMESPACE));
        
        htmlFormat = OutputFormat.createCompactFormat();
        htmlFormat.setXHTML(true);
        htmlFormat.setTrimText(false);
        htmlFormat.setPadText(true);
        htmlFormat.setNewlines(false);
        htmlFormat.setExpandEmptyElements(true);
        
        if (configuration.get(Configuration.XSL_POST_PROCESSOR) != null) {
            xslPostProcessor = Utils.toInstance(
                    configuration.get(Configuration.XSL_POST_PROCESSOR), injector);
        } else {
            xslPostProcessor = null;
        }
        if (configuration.get(Configuration.LOG_XML)) {
            Object obj = configuration.get(Configuration.XML_RESPONSE_LOGGER);
            if (obj instanceof XMLResponseLogger) {
                responseLogger = (XMLResponseLogger) obj;
            } else if (obj instanceof Class
                    && XMLResponseLogger.class.isAssignableFrom((Class<?>) obj)) {
                responseLogger = injector.getInstance((Class<XMLResponseLogger>) obj);
            } else {
                responseLogger = null;
            }
        } else {
            responseLogger = null;
        }
    }

    public void logXML(Document d) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer;

            StringWriter xml = new StringWriter();
            writer = new XMLWriter(xml, format);
            writer.write(d);

            responseLogger.logXML(xml.toString());

        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    protected Document getXSLDocument() {

        List<ResourceEntry> rootResources = ResourceScanner.findResources(
                rootResourcePaths, XSL_ACCEPTOR);

        ResourceEntry root = null;

        Iterator<ResourceEntry> iter = rootResources.iterator();
        if (iter != null) {
            while (iter.hasNext()) {
                ResourceEntry next = iter.next();
                if (next.getPath().endsWith("root.xsl")) {
                    iter.remove();
                    root = next;
                    break;
                }
            }
        }
        
        if (root == null) {
            throw new InternalWebApplicationException("root.xsl was not found");
        }

        List<ResourceEntry> resources = ResourceScanner.findResources(resourcePaths, XSL_ACCEPTOR);

        InputStream stream;
        SAXReader reader = new SAXReader();
        try {
            stream = root.getInputStream();
            Document document = reader.read(stream);
            stream.close();
            for (KeyValue<String, String> entry : namespaces) {
                document.getRootElement().addNamespace(entry.getKey(),
                        entry.getValue());
            }

            Element stylesheet = (Element) document
                    .selectSingleNode("//stylesheet");

            // Adding other stylesheets

            for (ResourceEntry file : resources) {
                if (file.getPath().endsWith(".xsl")) {
                    reader = new SAXReader();
                    stream = file.getInputStream();

                    try {
                        Document child = reader.read(stream);
                        for (Object el : child.getRootElement().elements()) {
                            if (el instanceof Node) {
                                stylesheet.add(((Node) el).detach());
                            }
                        }
                    } catch (DocumentException de) {
                        transformers.invalidate();
                        throw new WebApplicationException("Xsl-file " + file.getPath()
                                + " contains errors", de);
                    } finally {
                        stream.close();
                    }

                }
            }

            if (xslPostProcessor != null) {
                xslPostProcessor.process(document);
            }
            return document;
        } catch (DocumentException e) {
            throw new WebApplicationException(e);
        } catch (UnsupportedEncodingException e) {
            throw new WebApplicationException(e);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
    }

    public void sendResponse(Document document, HttpServletResponse resp,
            Mode mode) throws ServletException, IOException {
        if (responseLogger != null) {
            logXML(document);
        }
        if (mode != Mode.XML) {
            sendHTMLResponse(document, resp, mode);
        } else {
            sendXMLResponse(document, resp);
        }
    }

    private void sendXMLResponse(Document document, HttpServletResponse resp)
            throws IOException {
        resp.setContentType(Mode.XML.getContentType());
        resp.setHeader("Expires", "-1");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache, no-store");
        OutputFormat format = OutputFormat.createPrettyPrint();
        new XMLWriter(resp.getWriter(), format).write(document);
    }

    public void sendHTMLResponse(Document document, HttpServletResponse resp,
            Mode mode) throws ServletException, IOException {

        resp.setContentType(mode.getContentType());
        resp.setHeader("Expires", "-1");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache, no-store");

        if (!transformers.isInitialized()) {
            synchronized (transformers) {
                if (!transformers.isInitialized()) {
                    transformers.initialize(getXSLDocument());
                }
            }
        }

        Document rDocument = transformers.transform(document);
        
        if (mode == Mode.INIT) {
            rDocument.addDocType(
                "html",
                "-//W3C//DTD XHTML 1.0 Transitional//EN",
                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
        }
        
        new HTMLWriter(resp.getWriter(), htmlFormat).write(rDocument);
    }

    public void clean() {
        logger.debug("Reloading resources");
        transformers.invalidate();
    }
}
