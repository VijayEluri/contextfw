package net.contextfw.web.application.dom;

import java.util.ArrayList;
import java.util.List;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.converter.AttributeSerializer;
import net.contextfw.web.application.internal.component.ComponentBuilder;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

public final class DOMBuilder {

    private final Document document;
    private final AttributeSerializer<Object> serializer;
    private final Element root;
    private final ComponentBuilder componentBuilder;

    public DOMBuilder(String rootName, AttributeSerializer<Object> serializer, ComponentBuilder componentBuilder) {
        this.serializer = serializer;
        root = DocumentHelper.createElement(rootName);
        document = DocumentHelper.createDocument();
        document.setRootElement(root);
        root.add(DocumentHelper.createNamespace("txt", "http://contextfw.net/ns/txt"));
        this.componentBuilder = componentBuilder;
    }

    private DOMBuilder(Document document, Element root, AttributeSerializer<Object> serializer, ComponentBuilder componentBuilder) {
        this.document = document;
        this.root = root;
        this.serializer = serializer;
        this.componentBuilder = componentBuilder;
    }

//    public DOMBuilder child(String elementName, CSimpleElement element) {
//        descend(elementName).child(element);
//        return this;
//    }
    
    public DOMBuilder attr(String name, Object value) {
        root.addAttribute(name, serializer.serialize(value));
        return this;
    }
    
    public DOMBuilder child(Node element) {
        root.add(element);
        return this;
    }
    
    public DOMBuilder child(Object object, Object... buildins) {
        componentBuilder.build(this, object, buildins);
        return this;
    }

    public Element buildDOM() {
        return root;
    }

    public DOMBuilder findByXPath(String xpath) {
        Element element = (Element) root.selectSingleNode(xpath);
        if (element != null) {
            return new DOMBuilder(document, element, serializer, componentBuilder);    
        } else {
            return null;
        }
    }
    
    public DOMBuilder getByXPath(String xpath) {
        DOMBuilder b = findByXPath(xpath);
        if (b == null) {
            throw new WebApplicationException("Element for xpath '"+xpath+"' was not found");
        } 
        return b;
    }
    
    public List<DOMBuilder> listByXPath(String xpath) {
        List<DOMBuilder> rv = new ArrayList<DOMBuilder>();
        @SuppressWarnings("unchecked")
        List<Element> elements = (List<Element>) root.selectNodes(xpath);
        for (Element element : elements) {
            rv.add(new DOMBuilder(document, element, serializer, componentBuilder));
        }
        return rv;
    }
    
    public Document toDocument() {
         return document;
    }

    public DOMBuilder text(Object value) {
        root.setText(serializer.serialize(value));
        return this;
    }

    public DOMBuilder unparsed(String html) {
        descend("unparsed").root.setText(html);
        return this;
    }

    public DOMBuilder descend(String elementName) {
        return new DOMBuilder(document, root.addElement(elementName), serializer, componentBuilder);
    }
}