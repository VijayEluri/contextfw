package net.contextfw.web.application.properties;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AppendableClassProperty<F> extends BaseProperty<Set<Class<? extends F>>> 
    implements AppendableProperty<Set<Class<? extends F>>,Class<? extends F>> {

    public AppendableClassProperty(String key) {
        super(key);
    }

    @Override
    public String serialize(Collection<Class<? extends F>> value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Class<? extends F>> validate(Collection<Class<? extends F>> value) {
        return Collections.unmodifiableCollection(value);
    }

    @Override
    public Collection<Class<? extends F>> unserialize(String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Class<? extends F>> append(Collection<Class<? extends F>> collection, Class<? extends F> value) {
        Set<Class<? extends F>> rv = new HashSet<Class<? extends F>>();
        if (collection != null) {
            rv.addAll(collection);
        }
        rv.add(value);
        return rv;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Class<? extends F>> get(Collection<Class<? extends F>> value) {
        return (Collection<Class<? extends F>>) (value == null ? Collections.emptySet() : Collections.unmodifiableCollection(value));
    }
}
