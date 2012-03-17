package net.contextfw.web.commons.js;

import net.contextfw.web.application.DocumentProcessor;

import org.dom4j.Document;

public interface JsTemplateService extends DocumentProcessor {

    String NS = "http://www.contextfw.net/js";
    String PREFIX = "js";

    void process(Document document);

}