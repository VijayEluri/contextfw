package net.contextfw.web.application.internal.component;

import java.lang.reflect.Field;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.component.Element;

import com.google.inject.spi.InjectionListener;

public class AutoRegisterListener<I> implements InjectionListener<I> {

    @Override
    public void afterInjection(I injectee) {

        Class<?> currentClass = injectee.getClass();

        while (Component.class.isAssignableFrom(currentClass)) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (Component.class.isAssignableFrom(field.getType())) {
                    try {
                        Element annotation = field
                                .getAnnotation(Element.class);
                        Component component = (Component) field.get(injectee);
                        if (component != null && annotation != null
                                && annotation.autoRegister()) {
                            ((Component)injectee).registerChild(component);
                        }
                    } catch (IllegalArgumentException e) {
                        throw new WebApplicationException(e);
                    } catch (IllegalAccessException e) {
                        throw new WebApplicationException(e);
                    }
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }
}
