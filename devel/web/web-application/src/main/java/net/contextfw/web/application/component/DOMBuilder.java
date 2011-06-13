package net.contextfw.web.application.component;

import java.util.ArrayList;
import java.util.List;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.internal.component.ComponentBuilder;
import net.contextfw.web.application.serialize.AttributeSerializer;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * This class is responsible for actually building the DOM-tree during rendering phase.
 * 
 * <p>
 *  DOMBuilder can be accessed by using {@link CustomBuild} on component method.
 * </p>
 */
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
    
    /**
     * @param name
     *      Name of the attribute
     * @param value
     *      Value of the attribute. Is converted to String by using proper {@AttributeSerializer}.
     * @return
     *      Current DOMBuilder
     */
    public DOMBuilder attr(String name, Object value) {
        root.addAttribute(name, serializer.serialize(value));
        return this;
    }
    
    /**
     * Adds a child to this DOM-tree.
     * 
     * <p>
     *  This method can be used to add pre-existing DOM-trees to this DOM-tree.
     * </p>
     * 
     * @param element
     *    The element to be added
     * @return
     *    Current DOMBuilder
     */
    public DOMBuilder child(Node element) {
        root.add(element);
        return this;
    }
    
    /**
     * Adds a child to the DOM-tree
     * 
     * <p>
     *  This method is used to add Buildable-classes and Components into the DOM-tree.
     *  If object is not buildable, it will be run through <code>AttributeSerializer</code> 
     *  and added as text.
     * </p>
     * 
     * <h3>Buildins</h3>
     * 
     * <p>
     *  Buildin is similar concept to mixin.It is possible to add more objects to same DOM-tree. 
     *  The attributes and elements from each buildin are added as they were part of the
     *  original object. The wrapping classes are ignored for buildins.  
     * </p>
     * 
     * 
     * @param object
     *  The Object o be added
     * @param buildins
     *  The buildins to be added
     * @return
     *  Current DOMBuilder
     */
    public DOMBuilder child(Object object, Object... buildins) {
        componentBuilder.build(this, object, buildins);
        return this;
    }

    /**
     * Gets the root element where this DOMBuilder is at.
     * @return
     *  The current root element
     */
    public Element getCurrentRoot() {
        return root;
    }

    /**
     * Finds a path from current DOM-tree and returns a new DOMBuilder for it.
     * 
     * <p>
     *  This method is useful when DOM-tree has been built into to some point
     *  and needs to be changed.
     * </p>
     * 
     * @param xpath
     *  The xpath
     * @return
     *  new DOMBuilder or <code>null</code> if no path is matched
     */
    public DOMBuilder findByXPath(String xpath) {
        Element element = (Element) root.selectSingleNode(xpath);
        if (element != null) {
            return new DOMBuilder(document, element, serializer, componentBuilder);    
        } else {
            return null;
        }
    }

    /**
     * Gets a path from current DOM-tree and returns a new DOMBuilder for it.
     * 
     * <p>
     *  See documentation from <code>findXPath</code>
     * </p>
     * 
     * @param xpath
     *  The xpath
     * @return
     *  new DOMBuilder or throws exception if no path is matched
     */

    public DOMBuilder getByXPath(String xpath) {
        DOMBuilder b = findByXPath(xpath);
        if (b == null) {
            throw new WebApplicationException("Element for xpath '"+xpath+"' was not found");
        } 
        return b;
    }
    
    /**
     * Lists all paths from current DOM-tree and returns a new DOMBuilder for it.
     * 
     * <p>
     *  See documentation from <code>findXPath</code>
     * </p>
     * 
     * @param xpath
     *  The xpath
     * @return
     *  List of found DOMBuilders or empty list if nothing is found.
     */
    public List<DOMBuilder> listByXPath(String xpath) {
        List<DOMBuilder> rv = new ArrayList<DOMBuilder>();
        @SuppressWarnings("unchecked")
        List<Element> elements = (List<Element>) root.selectNodes(xpath);
        for (Element element : elements) {
            rv.add(new DOMBuilder(document, element, serializer, componentBuilder));
        }
        return rv;
    }
    
    /**
     * Returns the entire document of the DOM-tree
     */
    public Document toDocument() {
         return document;
    }

    /**
     * Adds text element to the dom-tree.
     * 
     * <p>
     *  The given argument is run through <code>AttributeSerializer</code>.
     * </p>
     * @param value
     * @return
     */
    public DOMBuilder text(Object value) {
        root.addText(serializer.serialize(value));
        return this;
    }

    /**
     * Adds a new child element and retuns a new DOMBuilder using the child as a root.
     *  
     * @param elementName
     *   Element name
     * @return
     *   New DOMBuilder using the created element as a root.
     */
    public DOMBuilder descend(String elementName) {
        return new DOMBuilder(document, root.addElement(elementName), serializer, componentBuilder);
    }
}