package net.contextfw.web.commons.minifier;

import net.contextfw.web.application.DocumentProcessor;

import org.dom4j.Document;

class MinifierServiceImpl implements MinifierService {

    private DocumentProcessor jsMinifier = null;
    
    private DocumentProcessor cssMinifier = null;
    
    private final boolean developmentMode;
    
    MinifierServiceImpl(boolean developmentMode) {
        this.developmentMode = developmentMode;
    }
    
    @Override
    public void process(Document document) {
        if (!developmentMode) {
            jsMinifier.process(document);
            cssMinifier.process(document);
        }
    }

    public void setJsMinifier(DocumentProcessor jsMinifier) {
        this.jsMinifier = jsMinifier;
    }

    public void setCssMinifier(DocumentProcessor cssMinifier) {
        this.cssMinifier = cssMinifier;
    }
}
