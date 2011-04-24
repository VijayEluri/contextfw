package net.contextfw.web.application.properties;

public interface UnsettableProperty<T> {
    /**
     * @return
     *   Returns the unique key of this property. Should be compatible with Java properties
     *   key
     */
    String getKey();
    
    T get(T value);
}
