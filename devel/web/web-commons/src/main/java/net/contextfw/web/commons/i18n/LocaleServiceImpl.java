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

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LocaleServiceImpl implements LocaleService {

    private Map<String, LocaleMessage> messages = new HashMap<String, LocaleMessage>();

    private final LocaleConf conf;

    private Map<Locale, ResourceBundle> bundles;

    @Inject
    public LocaleServiceImpl(LocaleConf conf) {
        this.conf = conf;
        reset();
    }

    private final ThreadLocal<Locale> current = new ThreadLocal<Locale>() {
        @Override
        protected Locale initialValue() {
            return conf.getDef();
        }
    };

    /* (non-Javadoc)
     * @see net.contextfw.web.commons.i18n.LocaleServiceI#setCurrentLocale(java.util.Locale)
     */
    @Override
    public void setCurrentLocale(Locale current) {
        this.current.set(current);
    }

    /* (non-Javadoc)
     * @see net.contextfw.web.commons.i18n.LocaleServiceI#reset()
     */
    @Override
    public final void reset() {
        bundles = new HashMap<Locale, ResourceBundle>();
        messages = new HashMap<String, LocaleMessage>();
        ResourceBundle.clearCache(Thread.currentThread().getContextClassLoader());
        for (Locale locale : conf.getLocales()) {
            bundles.put(locale, ResourceBundle.getBundle(
                        conf.getBaseName(),
                        locale,
                        Thread.currentThread().getContextClassLoader()));
        }
    }

    /* (non-Javadoc)
     * @see net.contextfw.web.commons.i18n.LocaleServiceI#process(org.dom4j.Document)
     */
    @Override
    public void process(Document document) {
        reset();
        List<Element> texts = getI18nElements(document);

        addLocalizations(document.getRootElement(),
                getLocalizations(getNames(texts), getBundles()));

        addConversions(document.getRootElement(), texts);
    }

    private void addConversions(Element root, List<Element> texts) {
        for (Element element : texts) {
            if (LocaleConf.NS.equals(element.getNamespaceURI())) {
                toCallTemplate(element);
            } else {
                @SuppressWarnings("unchecked")
                Iterator<Attribute> attributes = element.attributeIterator();
                int i = 0;
                while (attributes.hasNext()) {
                    Attribute attr = attributes.next();
                    if (LocaleConf.NS.equals(attr.getNamespaceURI())) {
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
                .addAttribute("name", LocaleConf.PREFIX + ":" + value);

        attribute.detach();

        element.elements().add(i, attribute);
    }

    private void toCallTemplate(Element element) {
        String name = element.getName();
        element.setName("xsl:call-template");
        element.addAttribute("name", LocaleConf.PREFIX + ":" + name);
    }

    @SuppressWarnings("unchecked")
    private List<Element> getI18nElements(Document document) {
        return document.getRootElement()
                .selectNodes("//*[namespace-uri()='" + LocaleConf.NS + "' or "
                        + "@*[namespace-uri()='" + LocaleConf.NS + "']]");
    }

    @SuppressWarnings("unchecked")
    private Set<String> getNames(List<Element> elements) {
        Set<String> names = new HashSet<String>();
        for (Element element : elements) {
            if (LocaleConf.NS.equals(element.getNamespaceURI())) {
                names.add(element.getName());
            }

            for (Attribute attribute : (List<Attribute>) element.attributes()) {
                if (LocaleConf.NS.equals(attribute.getNamespaceURI())) {
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
            template.addAttribute("name", LocaleConf.PREFIX + ":" + entry.getKey());
            //template.addAttribute("match", LocalizationConf.PREFIX + ":" + entry.getKey());
            Element choose = template.addElement("xsl:choose");
            for (Entry<Locale, String> text : entry.getValue().entrySet()) {
                choose.addElement("xsl:when")
                        .addAttribute("test", "$lang='" + text.getKey().getLanguage() + "'")
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
        for (Locale locale : conf.getLocales()) {
            bundles.put(locale, ResourceBundle.getBundle(
                    conf.getBaseName(),
                    locale,
                    Thread.currentThread().getContextClassLoader()));
        }
        return bundles;
    }

    private Map<Locale, String> getTexts(String name, Map<Locale, ResourceBundle> bundles) {
        Map<Locale, String> texts = new HashMap<Locale, String>();
        for (Locale locale : conf.getLocales()) {
            texts.put(locale, getText(name, locale));
        }
        return texts;
    }
    
    public LocaleMessage getMessage(String name) {
        LocaleMessage msg = messages.get(name);
        if (msg == null) {
            msg = new LocaleMessageImpl(this, name);
            messages.put(name, msg);
        }
        return msg;
    }

    public String getText(String name) {
        return getText(name, current.get());
    }

    public String getText(String name, Locale locale) {
        String text = null;
        try {
            text = bundles.get(locale).getString(name);
        } catch (MissingResourceException e) {
            if (conf.getDef() != null) {
                try {
                    text = bundles.get(conf.getDef()).getString(name);
                } catch (MissingResourceException e1) {
                    // Just ignore
                }
            }
        }
        if (text != null) {
            return text;
        } else if (conf.isStrict()) {
            throw new MissingResourceException("Localization missing: " + name,
                        conf.getBaseName(), name);
        } else {
            return "[missing(" + locale.getLanguage() + "):" + name + "]";
        }
    }
}
