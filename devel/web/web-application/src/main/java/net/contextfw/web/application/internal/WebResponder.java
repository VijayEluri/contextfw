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

import net.contextfw.web.application.DocumentProcessor;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.internal.configuration.KeyValue;
import net.contextfw.web.application.internal.util.ResourceEntry;
import net.contextfw.web.application.internal.util.ResourceScanner;
import net.contextfw.web.application.util.XMLResponseLogger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
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
        transformers = new Transformers(configuration.get(Configuration.TRANSFORMER_COUNT));
        resourcePaths.addAll(configuration.get(Configuration.RESOURCE_PATH));
        namespaces.addAll(configuration.get(Configuration.NAMESPACE));
        
        htmlFormat = OutputFormat.createCompactFormat();
        htmlFormat.setXHTML(true);
        htmlFormat.setTrimText(false);
        htmlFormat.setPadText(true);
        htmlFormat.setNewlines(false);
        htmlFormat.setExpandEmptyElements(true);
        
        if (configuration.get(Configuration.XSL_POST_PROCESSOR) != null) {
            xslPostProcessor = injector.getInstance(
                    configuration.get(Configuration.XSL_POST_PROCESSOR));
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

    protected String getXSLDocumentContent() {

        List<ResourceEntry> rootResources = ResourceScanner.findResources(
                rootResourcePaths, XSL_ACCEPTOR);

        ResourceEntry root = null;

        Iterator<ResourceEntry> iter = rootResources.iterator();

        while (iter.hasNext()) {
            ResourceEntry next = iter.next();
            if (next.getPath().endsWith("root.xsl")) {
                iter.remove();
                root = next;
                break;
            }
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

            StringWriter content = new StringWriter();
            OutputFormat format = OutputFormat.createCompactFormat();
            format.setXHTML(true);
            format.setTrimText(false);
            format.setPadText(true);
            format.setNewlines(false);
            XMLWriter writer = new XMLWriter(content, format);
            writer.write(document);
            return content.toString();
        } catch (DocumentException e) {
            throw new WebApplicationException(e);
        } catch (UnsupportedEncodingException e) {
            throw new WebApplicationException(e);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
    }

    // public Reader getXSLDocument() {
    // return new StringReader(getXSLDocumentContent());
    // }

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
            clean();
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
        logger.info("Reloading resources");
        transformers.initialize(getXSLDocumentContent());
    }
}
