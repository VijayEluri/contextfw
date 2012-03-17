package net.contextfw.web.commons.js;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Text;

import com.google.inject.Singleton;

/**
 * Provides means to read templates from XSL and transform them into Javascript.
 * 
 * <p>
 * This class provides means to read specific templates from XSL-document and
 * produce a set of javascript functions that contain the information of the
 * templates.
 * </p>
 * 
 * <p>
 * Note! At this point JsTemplate is considered experimental.
 * </p>
 * 
 * @since 0.8.1
 */
@Singleton
class JsTemplateServiceImpl implements JsTemplateService {

    private final JsTemplateServlet servlet;    
    
    public JsTemplateServiceImpl(JsTemplateServlet servlet) {
        this.servlet = servlet;
    }
    
    @SuppressWarnings("unchecked")
    private String generateTemplates(Document document) {
        List<Element> nodes = document.getRootElement().selectNodes("//js:template");
        String value = handleJSElements(nodes);
        for (Element el : nodes) {
            el.detach();
        }
        return value;
    }

    private String handleJSElements(List<Element> elements) {
        StringBuilder b = new StringBuilder();
        for (Element element : elements) {
            b.append(element.attributeValue("declaration")).append("{\n");
            StringBuilder buffer = new StringBuilder();
            b.append("var b = [];\n");
            for (Object obj : element.content()) {
                traverse(b, buffer, obj);
            }
            flushBuffer(b, buffer);
            b.append("return b.join('');\n");
            b.append("}\n");
        }

        return b.toString();
    }

    private void traverse(StringBuilder b, StringBuilder buffer, Object obj) {
        if (obj instanceof Text) {
            buffer.append(((Text) obj).getText());
        } else if (obj instanceof Element) {
            traverseEl(b, buffer, (Element) obj);
        } else {
            b.append(obj.getClass().getSimpleName()).append("\n");
        }
    }

    @SuppressWarnings("unchecked")
    private void traverseEl(StringBuilder b, StringBuilder buffer, Element element) {
        String name = element.getName();
        String prefix = element.getNamespacePrefix();
        if ("js".equals(prefix)) {
            flushBuffer(b, buffer);
            if ("script".equals(name)) {
                b.append(StringUtils.trimToEmpty(element.getText()) + "\n");
            } else if ("var".equals(name)) {
                b.append("b.push(" + element.attributeValue("name") + ");\n");
            }
        } else {
            buffer.append("<").append(name);
            addAttributes(b, buffer, element.attributes());

            String lName = name.toLowerCase(Locale.ENGLISH);

            if ("area".equals(lName) ||
                    "base".equals(lName) ||
                    "basefont".equals(lName) ||
                    "br".equals(lName) ||
                    "col".equals(lName) ||
                    "frame".equals(lName) ||
                    "hr".equals(lName) ||
                    "img".equals(lName) ||
                    "input".equals(lName) ||
                    "link".equals(lName) ||
                    "meta".equals(lName) ||
                    "param".equals(lName)) {

                buffer.append("/>");
            } else {
                buffer.append(">");
                for (Object obj : element.content()) {
                    traverse(b, buffer, obj);
                }
                buffer.append("</" + name + ">");
            }
        }
    }

    private void flushBuffer(StringBuilder b, StringBuilder buffer) {
        b.append("b.push('" + StringEscapeUtils.escapeJavaScript(buffer.toString()) + "');\n");
        buffer.setLength(0);
    }

    private void addAttributes(StringBuilder b, StringBuilder buffer, List<Attribute> attrs) {
        if (attrs.size() > 0) {
            for (Attribute attr : attrs) {
                if (attr.getNamespacePrefix().equals("js")) {
                    flushBuffer(b, buffer);
                    b.append("if (" + attr.getValue() + " != undefined) b.push(' " + attr.getName()
                            + "=\"'+" + attr.getValue() + "+'\"');\n");
                } else {
                    buffer.append(" " + attr.getName() + "=\"" + attr.getValue() + "\"");
                }
            }
        }
    }

    @Override
    public void process(Document document) {
        servlet.setContent(generateTemplates(document));
    }
}
