package net.contextfw.web.commons.i18n;

import java.util.Locale;

import net.contextfw.web.application.DocumentProcessor;

import org.dom4j.Document;

public interface LocaleService extends DocumentProcessor {

    void setCurrentLocale(Locale current);
    
    void reset();

    void process(Document document);

    LocaleMessage getMessage(String name);

}