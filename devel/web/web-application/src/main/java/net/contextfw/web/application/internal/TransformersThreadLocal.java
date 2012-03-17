package net.contextfw.web.application.internal;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

import net.contextfw.web.application.WebApplicationException;

class TransformersThreadLocal extends ThreadLocal<Transformer> {

    private final Templates templates;
    
    public TransformersThreadLocal(Templates templates) {
        this.templates = templates;
    }

    @Override
    protected Transformer initialValue() {
        try {
            return templates.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new WebApplicationException("Error while instantiating transformer", e);
        }
    }
}
