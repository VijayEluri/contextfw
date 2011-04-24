package net.contextfw.web.application.properties;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AppendableStringSetProperty extends BaseProperty<Set<String>> 
   implements AppendableProperty<Set<String>, String> {

    protected AppendableStringSetProperty(String key) {
        super(key);
    }

    @Override
    public Collection<String> unserialize(String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String serialize(Collection<String> value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<String> validate(Collection<String> value) {
        return Collections.unmodifiableCollection(value);
    }

    @Override
    public Set<String> append(Collection<String> collection, String value) {
        Set<String> rv = new HashSet<String>();
        if (collection != null) {
            rv.addAll(collection);
        }
        rv.add(value);
        return rv;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<String> get(Collection<String> value) {
        return (Collection<String>) (value == null ? Collections.emptySet() : value);
    }
}
