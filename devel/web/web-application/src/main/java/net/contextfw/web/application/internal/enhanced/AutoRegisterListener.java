package net.contextfw.web.application.internal.enhanced;

import java.lang.reflect.Field;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.elements.CElement;
import net.contextfw.web.application.elements.enhanced.EmbeddedElement;
import net.contextfw.web.application.elements.enhanced.EnhancedElement;

import com.google.inject.spi.InjectionListener;

public class AutoRegisterListener<I> implements InjectionListener<I> {

    @Override
    public void afterInjection(I injectee) {

        Class<?> currentClass = injectee.getClass();

        while (EnhancedElement.class.isAssignableFrom(currentClass)) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (CElement.class.isAssignableFrom(field.getType())) {
                    try {
                        EmbeddedElement annotation = field
                                .getAnnotation(EmbeddedElement.class);
                        CElement element = (CElement) field.get(injectee);
                        if (element != null && annotation != null
                                && annotation.autoRegister()) {
                            ((EnhancedElement)injectee).registerChild(element);
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
