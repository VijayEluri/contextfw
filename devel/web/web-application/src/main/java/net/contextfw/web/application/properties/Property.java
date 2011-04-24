package net.contextfw.web.application.properties;

/**
 * Represents generic property
 * 
 * @param <T>
 *   Type of the property
 */
public interface Property<T> extends UnsettableProperty<T> {
    
    /**
     * Unserializes value from String representation
     */
    T unserialize(String value);
    
    /**
     * Serializes value to String
     */ 
    String serialize(T value);
    
    /**
     * Validates the value
     * @param value
     *   The value
     * @return
     *   The value
     */
    T validate(T value);
}
