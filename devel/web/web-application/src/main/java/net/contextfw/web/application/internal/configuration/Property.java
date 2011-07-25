package net.contextfw.web.application.internal.configuration;

/**
 * Defines generic property
 * 
 * @param <T>
 *   Type of the property
 */
public interface Property<T> {
    
    /**
     * Returns the key for this property. 
     * 
     * <p>
     *  All information is stored by their keys thus keys must be unique.
     * </p>
     * @return
     */
    String getKey();
    
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
