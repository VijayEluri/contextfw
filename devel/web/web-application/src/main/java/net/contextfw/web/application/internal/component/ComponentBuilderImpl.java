package net.contextfw.web.application.internal.component;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Attribute;
import net.contextfw.web.application.component.Buildable;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.component.CustomBuild;
import net.contextfw.web.application.component.DOMBuilder;
import net.contextfw.web.application.component.Element;
import net.contextfw.web.application.component.ScriptElement;
import net.contextfw.web.application.internal.util.AttributeHandler;
import net.contextfw.web.application.lifecycle.AfterBuild;
import net.contextfw.web.application.lifecycle.BeforeBuild;
import net.contextfw.web.application.properties.Properties;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ComponentBuilderImpl implements ComponentBuilder {

	private static class MetaModel {
		public List<Method> beforeBuilds = new ArrayList<Method>();
		public List<Method> afterBuilds = new ArrayList<Method>();
		public List<Builder> builders = new ArrayList<Builder>();
		public List<Builder> updateBuilders = new ArrayList<Builder>();
		public List<Builder> partialBuilders = new ArrayList<Builder>();
		public String buildName;
		public Buildable annotation;
	}
	
//    private final Map<Class<?>, List<Method>> beforeBuilds = new HashMap<Class<?>, List<Method>>();
//    private final Map<Class<?>, List<Method>> afterBuilds = new HashMap<Class<?>, List<Method>>();
//    private final Map<Class<?>, List<Builder>> builders = new HashMap<Class<?>, List<Builder>>();
//    private final Map<Class<?>, List<Builder>> updateBuilders = new HashMap<Class<?>, List<Builder>>();
//    private final Map<Class<?>, List<Builder>> partialBuilders = new HashMap<Class<?>, List<Builder>>();
//    private final Map<Class<?>, String> buildNames = new HashMap<Class<?>, String>();
	private static final Map<Class<?>, MetaModel> metaModels = new HashMap<Class<?>, MetaModel>();
    private static final Map<Class<?>, Class<?>> actualClasses = new WeakHashMap<Class<?>, Class<?>>();
    
    private final AttributeHandler attributeHandler;
    
    //private final Map<Class<?>, Buildable> annotations = new HashMap<Class<?>, Buildable>();

    private final Gson gson;
    
    @Inject
    public ComponentBuilderImpl(AttributeHandler attributeHandler, Gson gson,
			Properties properties) {
		this.attributeHandler = attributeHandler;
		this.gson = gson;
	}

    private MetaModel getMetaModel(final Class<?> cl) {
    	Class<?> actual = actualClasses.get(cl);
    	if (actual == null) {
    		actual = getActualClass(cl);
    		actualClasses.put(cl, actual);
    	}
    	MetaModel model = metaModels.get(actual);
    	if (model == null) {
    		model = createMetaModel(actual);
    		metaModels.put(actual, model);
    	}
    	return model;
    }
    
	private MetaModel createMetaModel(final Class<?> cl) {
		try {
			MetaModel model = new MetaModel();
			Class<?> current = cl;
			
			while (current instanceof Object) {
                if (current.isAnnotationPresent(Buildable.class)) {
                    model.annotation = current.getAnnotation(Buildable.class);
                }
                current = current.getSuperclass();
            }
			
			if (model.annotation != null) {
				if (model.annotation.wrap()) {
					model.buildName = ("".equals(model.annotation.name()) ? 
						cl.getSimpleName() : model.annotation.name());
				}
				addEmbeddeds(model, cl);
			}
            return model;
        } catch (IllegalAccessException e) {
            throw new WebApplicationException(e);
        } catch (InvocationTargetException e) {
            throw new WebApplicationException(e);
        } catch (NoSuchMethodException e) {
            throw new WebApplicationException(e);
        } catch (IntrospectionException e) {
            throw new WebApplicationException(e);
        }
	}

//	private synchronized List<Builder> getBuilder(Class<?> cl) {
//		List<Builder> b = builders.get(cl);
//		if (b != null) {
//			return b;
//		} else {
//			createBuilders(cl);
//	        return builders.get(cl);
//        }
//    }

	public void clean() {
		metaModels.clear();
//		beforeBuilds.clear();
//		afterBuilds.clear();
//		builders.clear();
//		updateBuilders.clear();
//		partialBuilders.clear();
//		buildNames.clear();
		actualClasses.clear();
	}

//    private void createBuilders(Class<?> cl) {
//    	
//    	
//    	
//        builders.put(cl, new ArrayList<Builder>());
//        updateBuilders.put(cl, new ArrayList<Builder>());
//        partialBuilders.put(cl, new ArrayList<Builder>());
//
//        try {
//            addEmbeddeds(cl);
//        } catch (IllegalAccessException e) {
//            throw new WebApplicationException(e);
//        } catch (InvocationTargetException e) {
//            throw new WebApplicationException(e);
//        } catch (NoSuchMethodException e) {
//            throw new WebApplicationException(e);
//        } catch (IntrospectionException e) {
//            throw new WebApplicationException(e);
//        }
//    }

    private void addEmbeddeds(MetaModel model, Class<?> cl) throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, IntrospectionException {

        Class<?> currentClass = cl;

        while (currentClass instanceof Object) {

            for (Field field : currentClass.getDeclaredFields()) {

                PropertyAccess<Object> propertyAccess = new FieldPropertyAccess<Object>(field);
                String name = null;
                Builder builder = null;

                if (field.getAnnotation(Element.class) != null) {
                    Element element = field.getAnnotation(Element.class);
                    name = "".equals(element.name()) ? field.getName() : element.name();
                    builder = new ElementBuilder(this, propertyAccess, element.wrap() ? name : null, field.getName());
                    addToBuilders(model, element.onCreate(), element.onUpdate(), element.onPartialUpdate(), builder);
                } else if (field.getAnnotation(Attribute.class) != null) {
                    Attribute attribute = field.getAnnotation(Attribute.class);
                    name = "".equals(attribute.name()) ? field.getName() : attribute.name();
                    builder = new AttributeBuilder(propertyAccess, name, field.getName());
                    addToBuilders(model, attribute.onCreate(), attribute.onUpdate(), attribute.onPartialUpdate(), builder);
                } else if (field.getAnnotation(ScriptElement.class) != null) {
                	ScriptElement scriptElement = field.getAnnotation(ScriptElement.class);
                    name = scriptElement.wrapper();
                    builder = new ScriptElementBuilder(this, gson, propertyAccess, name, field.getName());
                    addToBuilders(model, scriptElement.onCreate(), scriptElement.onUpdate(), scriptElement.onPartialUpdate(), builder);
                }
            }

            for (Method method : currentClass.getDeclaredMethods()) {

                PropertyAccess<Object> propertyAccess = new MethodPropertyAccess(method);
                String name = null;
                Builder builder = null;

                if (method.getAnnotation(Element.class) != null) {
                    Element annotation = method.getAnnotation(Element.class);
                    name = "".equals(annotation.name()) ? method.getName() : annotation.name();
                    builder = new ElementBuilder(this, propertyAccess, annotation.wrap() ? name : null, method.getName());
                    addToBuilders(model, annotation.onCreate(), annotation.onUpdate(), annotation.onPartialUpdate(), builder);
                } else if (method.getAnnotation(Attribute.class) != null) {
                    Attribute annotation = method.getAnnotation(Attribute.class);
                    name = "".equals(annotation.name()) ? method.getName() : annotation.name();
                    builder = new AttributeBuilder(propertyAccess, name, method.getName());
                    addToBuilders(model, annotation.onCreate(), annotation.onUpdate(), annotation.onPartialUpdate(), builder);
                } else if (method.getAnnotation(CustomBuild.class) != null) {
                    CustomBuild annotation = method.getAnnotation(CustomBuild.class);
                    name = "".equals(annotation.name()) ? method.getName() : annotation.name();
                    builder = new MethodCustomBuilder(method, annotation.wrap() ? name : null);
                    addToBuilders(model, annotation.onCreate(), annotation.onUpdate(), annotation.onPartialUpdate(), builder);
                } else if (method.getAnnotation(ScriptElement.class) != null) {
                    	ScriptElement scriptElement = method.getAnnotation(ScriptElement.class);
                        name = scriptElement.wrapper();
                        builder = new ScriptElementBuilder(this, gson, propertyAccess, name, method.getName());
                        addToBuilders(model, scriptElement.onCreate(), scriptElement.onUpdate(), scriptElement.onPartialUpdate(), builder);
                } else if (method.getAnnotation(AfterBuild.class) != null) {
                    addAfterBuild(model, method);
                } else if (method.getAnnotation(BeforeBuild.class) != null) {
                    addBeforeBuild(model, method);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    private void addAfterBuild(MetaModel model, Method method) {
        model.afterBuilds.add(method);
    }

    private void addBeforeBuild(MetaModel model, Method method) {
        model.beforeBuilds.add(method);
    }

    private void addToBuilders(MetaModel model, boolean onCreate, boolean onUpdate, boolean onPartialUpdate, Builder builder) {
        if (onCreate) {
            model.builders.add(builder);
        }
        if (onUpdate) {
            model.updateBuilders.add(builder);
        }
        if (onPartialUpdate) {
            model.partialBuilders.add(builder);
        }
    }

//    private synchronized List<Builder> getUpdateBuilder(Class<?> cl) {
//        if (updateBuilders.containsKey(cl)) {
//            return updateBuilders.get(cl);
//        }
//        createBuilders(cl);
//        return updateBuilders.get(cl);
//    }
//
//    private synchronized List<Builder> getPartialUpdateBuilder(Class<?> cl) {
//        if (partialBuilders.containsKey(cl)) {
//            return partialBuilders.get(cl);
//        }
//        createBuilders(cl);
//        return partialBuilders.get(cl);
//    }

    public static Class<?> getActualClass(Object element) {
        return getActualClass(element.getClass());
    }
    
    public static Class<?> getActualClass(Class<?> cl) {
        Class<?> actual = actualClasses.get(cl);
        if (actual != null) {
        	return actual;
        } else {
        	actual = cl;
	        while (actual.getSimpleName().contains("EnhancerByGuice")) {
	            actual = actual.getSuperclass();
	        }
	        actualClasses.put(cl, actual);
	        return actual;
        }
    }
    
    @Override
    public String getBuildName(Class<?> cl) {
    	MetaModel model = getMetaModel(cl);
    	if (model.annotation != null) {
    		return model.buildName;
        } else {
            throw new WebApplicationException("Class " + cl.getName() + " is not buildable");
        }
    }

    @Override
    public void build(DOMBuilder sb, Object component, Object... buildins) {
    	MetaModel model = getMetaModel(component.getClass());
    	if (model.annotation != null) {
            DOMBuilder b = model.buildName == null ? sb : sb.descend(model.buildName);
            build(model, b, component, model.builders, false, null, buildins);
        } else {
            sb.text(attributeHandler.serialize(component));
        }
    }

	private void build(MetaModel model, DOMBuilder b, Object component,
			List<Builder> builders, boolean partial, Set<String> updates,
			Object... buildins) {
		if (component instanceof Component) {
			if (!((Component) component).isEnabled()) {
				return;
			}
		}
		for (Method method : model.beforeBuilds) {
			try {
				method.invoke(component);
			} catch (IllegalArgumentException e) {
				throw new WebApplicationException(e);
			} catch (IllegalAccessException e) {
				throw new WebApplicationException(e);
			} catch (InvocationTargetException e) {
				throw new WebApplicationException(e);
			}
		}

		if (partial) {
			for (Builder builder : builders) {
				if (builder.isUpdateBuildable(updates)) {
					builder.build(b, component);
				}
			}
		} else {
			for (Builder builder : builders) {
				builder.build(b, component);
			}
		}

		// Handling buildins

		if (buildins != null) {
			for (Object buildIn : buildins) {
				if (buildIn != null) {
					MetaModel bmodel = getMetaModel(buildIn.getClass());
					if (bmodel.annotation != null) {
						for (Builder builder : bmodel.builders) {
							builder.build(b, buildIn);
						}
					}
				}
			}
		}

		for (Method method : model.afterBuilds) {
			try {
				method.invoke(component);
			} catch (IllegalArgumentException e) {
				throw new WebApplicationException(e);
			} catch (IllegalAccessException e) {
				throw new WebApplicationException(e);
			} catch (InvocationTargetException e) {
				throw new WebApplicationException(e);
			}
		}
	}

    @Override
    public void buildUpdate(DOMBuilder sb, Component component, String updateName) {
    	MetaModel model = getMetaModel(component.getClass());
        if (model.annotation != null) {
            DOMBuilder b = sb.descend(model.buildName + "." + updateName);
            build(model, b, component, model.updateBuilders, false, null, (Object[]) null);
        }
    }

    @Override
    public void buildPartialUpdate(DOMBuilder sb, Component component, String updateName, Set<String> updates) {
    	MetaModel model = getMetaModel(component.getClass());
        if (model.annotation != null) {
            DOMBuilder b = sb.descend(model.buildName + "." + updateName);
            build(model, b, component, model.partialBuilders, true, updates, (Object[]) null);
        }
    }

    @Override
    public boolean isBuildable(Class<?> cl) {
    	return getMetaModel(cl).annotation != null;
    }
}