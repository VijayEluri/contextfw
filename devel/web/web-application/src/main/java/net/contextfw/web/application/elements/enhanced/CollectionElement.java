package net.contextfw.web.application.elements.enhanced;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CSimpleElement;

public class CollectionElement<T> implements CSimpleElement {

    private final String itemName;
    private final Builder<T> itemBuilder;
    private Iterable<T> collection;
    
    public CollectionElement(String itemName, Builder<T> itemBuilder) {
        this.itemName = itemName;
        this.itemBuilder = itemBuilder;
    }
    
    public CollectionElement(Builder<T> itemBuilder) {
        this(null, itemBuilder);
    }

    @Override
    public void build(DOMBuilder b) {
        if (collection != null) {
            for (T t : collection) {
                DOMBuilder itemb = itemName == null ? b : b.descend(itemName);
                itemBuilder.build(itemb, t);
            }
        }
    }

    public void setCollection(Iterable<T> collection) {
        this.collection = collection;
    }
}
