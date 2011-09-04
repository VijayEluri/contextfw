package net.contextfw.web.commons.js;

import net.contextfw.web.application.DocumentProcessor;

import org.dom4j.Document;

public interface JsTemplateService extends DocumentProcessor {

    public static final String NS = "http://www.contextfw.net/js";
    public static final String PREFIX = "js";

    void process(Document document);

}