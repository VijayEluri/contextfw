package net.contextfw.web.application.dom;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class ObjectBuilder<T> {

    private final Set<Field> attributeFields = new HashSet<Field>();
    private final Set<Field> textFields = new HashSet<Field>();;

    private ObjectBuilder() {}
    
    public void build(DOMBuilder b, T t) {
        try {
            for (Field field : attributeFields) {
                b.attr(field.getName(), field.get(t));
            }
            for (Field field : textFields) {
                b.descend(field.getName()).text(field.get(t));
            }
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    public static <T> ObjectBuilder<T> forClass (Class<T> clazz) {
        ObjectBuilder<T> builder = new ObjectBuilder<T>();
        
        Class<?> currentClass = clazz;

        while (currentClass instanceof Object) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (isUsable(field)) {
                    field.setAccessible(true);
                    builder.attributeFields.add(field);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        
        return builder;
    }

    private static boolean isUsable(Field field) {
        return !field.getName().equals("serialVersionUID") && !field.getType().isArray() 
                && !Collection.class.isAssignableFrom(field.getType());
    }
    
    public ObjectBuilder<T> asText(String fieldName) {
        
        Field textField = null;
        
        for (Field attr : attributeFields) {
            if (attr.getName().equals(fieldName)) {
                textField = attr;
                break;
            }
        }
        
        if (textField == null) {
            throw new RuntimeException("Could not find field " + fieldName);
        }
        else {
            attributeFields.remove(textField);
            textFields.add(textField);
            return this;
        }
    }
    
    public ObjectBuilder<T> ignore(String fieldName) {
        
        Field textField = null;
        
        for (Field attr : attributeFields) {
            if (attr.getName().equals(fieldName)) {
                textField = attr;
                break;
            }
        }
        
        if (textField == null) {
            throw new RuntimeException("Could not find field " + fieldName);
        }
        else {
            attributeFields.remove(textField);
            textFields.remove(textField);
            return this;
        }
    }
}