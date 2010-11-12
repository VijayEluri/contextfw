package net.contextfw.web.application.dom;

import net.contextfw.web.application.converter.AttributeSerializer;
import net.contextfw.web.application.internal.component.ComponentBuilder;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

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