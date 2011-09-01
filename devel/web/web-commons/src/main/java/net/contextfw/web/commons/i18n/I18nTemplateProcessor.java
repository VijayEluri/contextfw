package net.contextfw.web.commons.i18n;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import net.contextfw.web.application.DocumentProcessor;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * This class can be used to create localization convention for standard 
 * Java resource-bundles.
 * 
 * <p>
 *  When document is processed, all i18n-namespaced tokens are scanned
 *  and are mapped to localized texts. 
 * </p>
 * 
 *
 */
public class I18nTemplateProcessor implements DocumentProcessor {

    public static final String NS = "http://www.contextfw.net/i18n";
    public static final String PREFIX = "i18n";
    
    private final String baseName;
    private final List<Locale> locales;
    private final Locale fallback;
    private final boolean strict;
    
    public I18nTemplateProcessor(String baseName, 
                                 List<Locale> locales, 
                                 Locale fallback,
                                 boolean strict) {
        
        this.baseName = baseName;
        this.locales = locales;
        this.fallback = fallback;
        this.strict = strict;
    }
    
    @Override
    public void process(Document document) {
        
        List<Element> texts = getI18nElements(document);
        
        addLocalizations(document.getRootElement(), 
                getLocalizations(getNames(texts), getBundles()));
        
        addConversions(document.getRootElement(), texts);
    }
    
    private void addConversions(Element root, List<Element> texts) {
        for (Element element : texts) {
            if (NS.equals(element.getNamespaceURI())) {
                toCallTemplate(element);
            } else {
                @SuppressWarnings("unchecked")
                Iterator<Attribute> attributes = element.attributeIterator();
                int i = 0;
                while (attributes.hasNext()) {
                    Attribute attr = attributes.next();
                    if (NS.equals(attr.getNamespaceURI())) {
                        toCallTemplate(i, element, attr);
                        attributes.remove();
                        i++;
                    }
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void toCallTemplate(int i, Element element, Attribute attr) {
        String name = attr.getName();
        String value = attr.getValue();
        
        Element attribute = element
            .addElement("xsl:attribute")
            .addAttribute("name", name);
        
        attribute.addElement("xsl:call-template")
            .addAttribute("name", PREFIX + ":" + value);
        
        attribute.detach();
        
        element.elements().add(i, attribute);
    }

    private void toCallTemplate(Element element) {
        String name = element.getName();
        element.setName("xsl:call-template");
        element.addAttribute("name", PREFIX + ":" + name);
    }

    @SuppressWarnings("unchecked")
    private List<Element> getI18nElements(Document document) {
        return document.getRootElement()
            .selectNodes("//*[namespace-uri()='"+NS+"' or "
              + "@*[namespace-uri()='"+NS+"']]");
    }
    
    @SuppressWarnings("unchecked")
    private Set<String> getNames(List<Element> elements) {
        Set<String> names = new HashSet<String>();
        for (Element element : elements) {
            if (NS.equals(element.getNamespaceURI())) {
                names.add(element.getName());
            }
            
            for (Attribute attribute : (List<Attribute>) element.attributes()) {
                if (NS.equals(attribute.getNamespaceURI())) {
                    names.add(attribute.getValue());
                }
            }
        }
        return names;
    }
    
    private void addLocalizations(Element root, 
                                  Map<String, Map<Locale, String>> localizations) {
        
        for (Entry<String, Map<Locale, String>> entry : localizations.entrySet()) {
            Element template = root.addElement("xsl:template");
            template.addAttribute("name", PREFIX + ":" + entry.getKey());
            Element choose = template.addElement("xsl:choose");
            for (Entry<Locale, String> text : entry.getValue().entrySet()) {
                choose.addElement("xsl:when")
                      .addAttribute("test", "$lang='"+text.getKey().getLanguage()+"'")
                      .addText(text.getValue());
            }
        }
    }
    
    private Map<String, Map<Locale, String>> getLocalizations(
            Set<String> names,
            Map<Locale, ResourceBundle> bundles) {
        
        Map<String, Map<Locale, String>> localizations = 
            new HashMap<String, Map<Locale, String>>();
        
        for (String name : names) {
            localizations.put(name, getTexts(name, bundles));
        }
        
        return localizations;
    }
    
    private Map<Locale, ResourceBundle> getBundles() {
        Map<Locale, ResourceBundle> bundles = new HashMap<Locale, ResourceBundle>();
        ResourceBundle.clearCache(Thread.currentThread().getContextClassLoader());
        for (Locale locale : locales) {
            bundles.put(locale, ResourceBundle.getBundle(
                    baseName,
                    locale,
                    Thread.currentThread().getContextClassLoader())); 
        }
        return bundles;
    }
    
    private Map<Locale, String> getTexts(String name, Map<Locale, ResourceBundle> bundles) {
        Map<Locale, String> texts = new HashMap<Locale, String>();
        for (Locale locale : locales) {
            String text = null;
            try {
                text = bundles.get(locale).getString(name);
            } catch (MissingResourceException e) {
                if (fallback != null) {
                    try {
                        text = bundles.get(fallback).getString(name);
                    } catch (MissingResourceException e1) {
                        // Just ignore
                    }
                }
            } finally {
                if (text != null) {
                    texts.put(locale, text);
                } else if (strict) {
                    throw new MissingResourceException("Localization missing: " + name, 
                            baseName, name);
                } else {
                    texts.put(locale, "[missing("+locale.getLanguage()+"):"+name+"]");
                }
            }
        }
        return texts;
    }
}
