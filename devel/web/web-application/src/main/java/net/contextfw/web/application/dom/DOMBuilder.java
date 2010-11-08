package net.contextfw.web.application.dom;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.internal.component.ComponentBuilder;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public final class DOMBuilder {

    private final Document document;
    private final AttributeHandler attributes;
    private final Element root;
    private final ComponentBuilder componentBuilder;

    public DOMBuilder(String rootName, AttributeHandler attributes, ComponentBuilder componentBuilder) {
        this.attributes = attributes;
        root = DocumentHelper.createElement(rootName);
        document = DocumentHelper.createDocument();
        document.setRootElement(root);
        root.add(DocumentHelper.createNamespace("txt", "http://contextfw.net/ns/txt"));
        this.componentBuilder = componentBuilder;
    }

    private DOMBuilder(Document document, Element root, AttributeHandler attributes, ComponentBuilder componentBuilder) {
        this.document = document;
        this.root = root;
        this.attributes = attributes;
        this.componentBuilder = componentBuilder;
    }

//    public DOMBuilder child(String elementName, CSimpleElement element) {
//        descend(elementName).child(element);
//        return this;
//    }
    
    public DOMBuilder attr(String name, Object value) {
        root.addAttribute(name, attributes.toString(value));
        return this;
    }
    
    public DOMBuilder child(Element element) {
        root.add(element);
        return this;
    }
    
    public DOMBuilder child(Object object) {
        componentBuilder.build(this, object);
        return this;
    }

    public Element buildDOM() {
        return root;
    }

    public Document toDocument() {
         return document;
    }

    public DOMBuilder text(Object value) {
        root.setText(attributes.toString(value));
        return this;
    }

    public DOMBuilder unparsed(String html) {
        descend("unparsed").root.setText(html);
        return this;
    }

    public DOMBuilder descend(String elementName) {
        return new DOMBuilder(document, root.addElement(elementName), attributes, componentBuilder);
    }
}