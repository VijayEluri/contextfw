package net.contextfw.web.application.properties;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.contextfw.web.application.DocumentProcessor;
import net.contextfw.web.application.PropertyProvider;
import net.contextfw.web.application.SystemPropertyProvider;
import net.contextfw.web.application.lifecycle.DefaultLifecycleListener;
import net.contextfw.web.application.lifecycle.LifecycleListener;
import net.contextfw.web.application.serialize.AttributeJsonSerializer;
import net.contextfw.web.application.serialize.AttributeSerializer;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

/**
 * This class defines the global system properties which are set during initialization.
 * 
 * <h3>Note</h3>
 * 
 * <p>
 *  This class is immutable thus modifying or adding properties returns always a new instance
 *  of <code>Properties</code>.
 * </p>
 *
 */
public class Properties {
    
    private static final String KEY_NAMESPACE = "contextfw.namespace";
    
//    private static final String KEY_CREATE_HTTP_HEADER = "contextfw.createHttpHeader";
    
//    private static final String KEY_UPDATE_HTTP_HEADER = "contextfw.updateHttpHeader";

    private static final String KEY_ATTRIBUTE_SERIALIZER = "contextfw.attributeSerializer";

    private static final String KEY_JSON_SERIALIZER = "contextfw.jsonSerializer";

    private static final String KEY_JSON_DESERIALIZER = "contextfw.jsonDeserializer";

    private static final String KEY_ATTRIBUTE_JSON_SERIALIZER = "contextfw.attributeJsonSerializer";

    private static final String KEY_REMOVAL_SCHEDULE_PERIOD = "contextfw.removalSchedulePeriod";

   // private static final String KEY_POLL_TIME = "contextfw.pollTime";

    private static final String KEY_MAX_INACTIVITY = "contextfw.maxInactivity";

    private static final String KEY_INITIAL_MAX_INACTIVITY = "contextfw.initialMaxInactivity";

  //  private static final String KEY_ERROR_TIME = "contextfw.errorTime";

    private static final String KEY_VIEW_COMPONENT_ROOT_PACKAGE = "contextfw.viewComponentRootPackage";

    private static final String KEY_RESOURCE_PATH = "contextfw.resourcePath";

    private static final String KEY_TRANSFORMER_COUNT = "contextfw.transformerCount";

    private static final String KEY_LIFECYCLE_LISTENER = "contextfw.lifecycleListener";

    private static final String KEY_PROPERTY_PROVIDER = "contextfw.propertyProvider";
    
    private static final String KEY_XSL_POST_PROCESSOR = "contextfw.xslPostProcessor";

    private static final String KEY_XML_PARAM_NAME = "contextfw.xmlParamName";

    //private static final String KEY_CONTEXT_PATH = "contextfw.contextPath";

    private static final String KEY_RESOURCES_PREFIX = "contextfw.resourcesPrefix";

    private static final String KEY_LOG_XML = "contextfw.logXML";

    private static final String KEY_DEVELOPMENT_MODE = "contextfw.developmentMode";

    /**
     * Creates the default configuration.
     * 
     * <p>
     *  This is the recommended way to initialize properties.
     * </p>
     */
    public static Properties getDefaults() {
        return new Properties()
          .set(DEVELOPMENT_MODE, true)
          .set(LOG_XML, true)
          .set(TRANSFORMER_COUNT, 1)
          .set(RESOURCES_PREFIX, "/resources")
          //.set(CONTEXT_PATH, "")
          .set(XML_PARAM_NAME, null)
          .set(PROPERTY_PROVIDER, new SystemPropertyProvider())
          .set(LIFECYCLE_LISTENER, DefaultLifecycleListener.class)
          .set(RESOURCE_PATH, new HashSet<String>())
          .set(VIEW_COMPONENT_ROOT_PACKAGE, new HashSet<String>())
          // .set(ERROR_TIME.inMinsAndSecs(1, 30))
          .set(INITIAL_MAX_INACTIVITY.inSeconds(30))
          //.set(POLL_TIME.inSeconds(70))
          .set(REMOVAL_SCHEDULE_PERIOD.inMinutes(1))
          .set(MAX_INACTIVITY.inMinutes(2))
          .set(NAMESPACE, new HashSet<KeyValue<String, String>>())
          .set(ATTRIBUTE_JSON_SERIALIZER, new HashSet<KeyValue<Class<?>, 
                     Class<? extends AttributeJsonSerializer<?>>>>())
           .set(JSON_SERIALIZER, new HashSet<KeyValue<Class<?>, 
                     Class<? extends JsonSerializer<?>>>>())
          .set(JSON_DESERIALIZER, new HashSet<KeyValue<Class<?>, 
                     Class<? extends JsonDeserializer<?>>>>())
          .set(ATTRIBUTE_SERIALIZER, new HashSet<KeyValue<Class<?>, 
                     Class<? extends AttributeSerializer<?>>>>());
    }
    
    /**
     * Defines whether system is run in development mode or not. 
     * 
     * <p>
     * In development mode resource changes are actively tracked during each page load or update.
     * </p>
     * <p>
     *  Default: <code>true</code>
     * </p>
     */
    public static final SettableProperty<Boolean> DEVELOPMENT_MODE = 
        new BooleanProperty(KEY_DEVELOPMENT_MODE);
    
    /**
     * Defines whether the XML-representation of page load or update are logged. Only suitable
     * during development mode.
     * 
     * <p>
     *  Default: <code>true</code>
     * </p>
     */
    public static final SettableProperty<Boolean> LOG_XML = 
        new BooleanProperty(KEY_LOG_XML);
    
    /**
     * Defines the prefix for javascript- and css-files that are loaded with each page.
     * 
     * <p>
     *  Default: <code>/resources</code>
     * </p>
     */
    public static final SettableProperty<String> RESOURCES_PREFIX = 
        new StringProperty(KEY_RESOURCES_PREFIX);
    
//    /**
//     * Defines the http context path where the system is installed.
//     * 
//     * <p>
//     *  This information is important for all resources that should be accessible from
//     *  web page. Uses standard conventions. In XSL-templates the value of this property
//     *  is accessible through variable <code>$contextPath</code>
//     * </p>
//     * <p>
//     *  Example: <code>&lt;img src="{$contextPath}/images/image.jpg" /&gt;</code>
//     * </p>
//     * <p>
//     *  Default: empty string
//     * </p>
//     */
//    public static final SettableProperty<String> CONTEXT_PATH = 
//        new StringProperty(KEY_CONTEXT_PATH);
    
    /**
     * Besides property <code>LOG_XML</code> it is possible to see the the page XML-representation
     * on web client. 
     * 
     * <p>
     *  This property defines an URL-parameter that is used to trigger the behavior. Note that the
     *  parameter value is irrelevant, the existence of the parameter is enough.
     * </p>
     * <p>
     *  If the value of this property is set to <code>null</code> this feature is disabled.
     * </p>
     * <p>
     *  Default: <code>xml</code>
     * </p>
     */
    public static final SettableProperty<String> XML_PARAM_NAME = 
        new StringProperty(KEY_XML_PARAM_NAME);
    
    
    /**
     * Defines the provider that is used to inject system properties to the system.
     * 
     * <p>
     *  This property takes a sub class of {@link PropertyProvider}.
     * </p>
     * <p>
     *  Default: {@link SystemPropertyProvider}
     * </p>  
     */
    public static final SettableProperty<PropertyProvider> PROPERTY_PROVIDER = 
        new ObjectProperty<PropertyProvider>(KEY_PROPERTY_PROVIDER);
    
    /**
     * Binds a lifecycle listener to the system
     */
    public static final SettableProperty<Class<? extends LifecycleListener>> LIFECYCLE_LISTENER = 
        new ClassProperty<LifecycleListener>(KEY_LIFECYCLE_LISTENER);
    
    /**
     * Binds a XSL-postprocessor to the system
     */
    public static final SettableProperty<Class<? extends DocumentProcessor>> XSL_POST_PROCESSOR = 
        new ClassProperty<DocumentProcessor>(KEY_XSL_POST_PROCESSOR);
    
    /**
     * Defines the number of transformers that are used to render XSL into XHTML.
     * 
     * <p>
     *  Transformers are not thread safe thus access to each transformer must be synchronized.
     *  It is possible to increase performance little by adding more transformers. It should be noted however
     *  that each transformer needs to read all templates to memory thus the amount
     *  of used memory is a multiple of the transformer count.
     * </p>
     * <p>
     *  In practice a few transformer, max 5 is probably more than enough.
     * </p>
     * <p>
     *  Default: <code>1</code>
     * </p>
     */
    public static final SettableProperty<Integer> TRANSFORMER_COUNT =
        new RangedIntegerProperty(KEY_TRANSFORMER_COUNT, 1, 200);
    
    /**
     * Defines the root paths that contains components' css- and javascript-resources.
     * 
     * <p>
     *  Determined paths and their sub folder are scanned for all resources and are returned as part
     *  of a page.
     * </p>
     * 
     * <p>
     *  The value of the property can mean class package or directory. By default
     *  value interpreted as package, but by adding a prefix <code>file:</code> value is
     *  interpreted as directory path.
     * 
     */
    public static final AddableProperty<Set<String>, String> RESOURCE_PATH
        = new StringSetProperty(KEY_RESOURCE_PATH);
    
    /**
     * Defines the root package from within view packages are scanned.
     * 
     * <p>
     *  Default: No default value
     * </p>
     */
    public static final AddableProperty<Set<String>, String> VIEW_COMPONENT_ROOT_PACKAGE
        = new StringSetProperty(KEY_VIEW_COMPONENT_ROOT_PACKAGE);

    /**
     * Defines the initial maximum inactivity until page scope is expired.
     * 
     * <p>
     *  This property is used to expire page scope early for bots and web clients incapable of using
     *  javascript, to make sure that resources are freed. 
     * </p>
     * 
     * <p>
     *  Recommended values range from 30 seconds to 5 minutes. For mobile clients it is preferred
     *  to use higher values.
     * </p>
     * 
     * <p>
     *  Default: 30 seconds
     * </p>
     */
    public static final TemporalProperty INITIAL_MAX_INACTIVITY = 
        new TemporalProperty(KEY_INITIAL_MAX_INACTIVITY);
    
    /**
     * Defines the maximum inactivity until page scope is expired.
     * 
     * <p>
     *  This property is used to expire page scope if no activity is taken in page for given maximum time. 
     *  In normal circumstances page scope is expired automatically when page is unloaded. However,
     *  in cases of network failure or misuse expiration may never be triggered. 
     * </p>
     * 
     * <p>
     *  The values of this property can range from minutes to hours depdening on need. If inactivity is 
     *  defined low (&lt; 10 minutes) system is quite safe from misuse but is more intolerable to temporary
     *  network failures and requires constant refreshing. If higher values are used (&gt; 1 hour) it is
     *  recommended to use bandwidth throttling stategies to prevent misuse.
     * </p>
     * 
     * <p>
     *  Default: 2 minutes 
     * </p>
     */
    public static final TemporalProperty MAX_INACTIVITY = 
        new TemporalProperty(KEY_MAX_INACTIVITY);
    
    /**
     * Defines the the period how often expired page scopes are purged from memory.
     * 
     * <p>
     *  There should be no need to touch this property.
     * </p>
     * <p>
     *  Default: 1 minute
     * </p>
     */
    public static final TemporalProperty REMOVAL_SCHEDULE_PERIOD = 
        new TemporalProperty(KEY_REMOVAL_SCHEDULE_PERIOD);
    
    /**
     * Defines additional namespaces to be used in XSL-templates.
     * 
     * <p>
     *  If XSL-templates are using additional namespaces they must be registered here. 
     *  The namespaces are added to the master template.
     * </p>
     */
    public static final SelfKeyValueSetProperty<String, String>
        NAMESPACE
        = new SelfKeyValueSetProperty<String, String>(KEY_NAMESPACE);
    
//    public static final SelfKeyValueSetProperty<String, String>
//      CREATE_HTTP_HEADER
//      = new SelfKeyValueSetProperty<String, String>(KEY_CREATE_HTTP_HEADER);
//    
//    public static final SelfKeyValueSetProperty<String, String>
//      UPDATE_HTTP_HEADER
//    = new SelfKeyValueSetProperty<String, String>(KEY_UPDATE_HTTP_HEADER);
    
    
    /**
     * Binds a new AttributeJsonSerialiser to the system
     */
    public static final SelfKeyValueSetProperty<Class<?>, 
        Class<? extends AttributeJsonSerializer<?>>> ATTRIBUTE_JSON_SERIALIZER
           = new SelfKeyValueSetProperty<Class<?>, 
              Class<? extends AttributeJsonSerializer<?>>>(KEY_ATTRIBUTE_JSON_SERIALIZER 
              );
    
    /**
     * Binds a new Json deserialiser to the system
     */
    public static final SelfKeyValueSetProperty<Class<?>, 
    Class<? extends JsonDeserializer<?>>> JSON_DESERIALIZER
        = new SelfKeyValueSetProperty<Class<?>, 
            Class<? extends JsonDeserializer<?>>>(KEY_JSON_DESERIALIZER);
    
    /**
     * Binds a new Json serializer to the system
     */
    public static final SelfKeyValueSetProperty<Class<?>, 
    Class<? extends JsonSerializer<?>>> JSON_SERIALIZER
        = new SelfKeyValueSetProperty<Class<?>, 
            Class<? extends JsonSerializer<?>>>(KEY_JSON_SERIALIZER);
    
    /**
     *  Binds a new attribute serializer
     */
    public static final SelfKeyValueSetProperty<Class<?>, 
    Class<? extends AttributeSerializer<?>>> ATTRIBUTE_SERIALIZER
        = new SelfKeyValueSetProperty<Class<?>, 
            Class<? extends AttributeSerializer<?>>>(KEY_ATTRIBUTE_SERIALIZER);

    
    private final Map<String, Object> values;

    /**
     * Constructs a new property. Not recommended for normal usage.
     */
    public Properties() {
        values = new HashMap<String, Object>();
    }
    
    private <T> Properties(Map<String, Object> values, Property<T> property, T value) {
        this.values = new HashMap<String, Object>();
        this.values.putAll(values);
        this.values.put(property.getKey(), property.validate(value));
    }
    
    /**
     * Returns the value of given property
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Property<T> property) {
        return property.validate((T) values.get(property.getKey()));
    }
    /**
     * Set a new property.
     * 
     * <p>
     *  Previosly set property get overriden.
     * </p>
     */
    public <T> Properties set(SettableProperty<T> property, T value) {
        return new Properties(values, property, value);
    }
    
    /**
     * Set a new property.
     * 
     * <p>
     *  Previosly set property get overriden.
     * </p>
     */
    public <T extends Collection<V>, V> Properties set(AddableProperty<T, V> property, T value) {
        return new Properties(values, property, value);
    }
    
    /**
     * Set a new property.
     * 
     * <p>
     *  Previosly set property get overriden.
     * </p>
     */
    public <T extends Collection<V>, V> Properties set(SelfAddableProperty<T, V> property, T value) {
        return new Properties(values, property, value);
    }
    
    /**
     * Set a new property.
     * 
     * <p>
     *  Previosly set property get overriden.
     * </p>
     */
    public <T> Properties set(SelfSettableProperty<T> property) {
        return new Properties(values, property, property.getValue());
    }
    
    /**
     * Adds a new property
     */
    public <T extends Collection<V>, V> Properties add(AddableProperty<T, V> property, V value) {
        return new Properties(values, property, property.add(get(property), value));
    }
    
    /**
     * Adds a new property
     */
    public <T extends Collection<V>, V> Properties add(SelfAddableProperty<T, V> property) {
        return new Properties(values, property, property.add(get(property), property.getValue()));
    }
    
//    public <K, V> Properties add(KeyValueSetProperty<K, V> property) {
//        return new Properties(values, property, property.append(
//                get(property), property.getValue()));
//    }
}