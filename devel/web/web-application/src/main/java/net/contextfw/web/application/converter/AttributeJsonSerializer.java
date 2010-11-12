package net.contextfw.web.application.converter;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * A convinience class to handle attribute and Json serialization at the same time
 * 
 * <p>
 *  This class can be used to do all serialization for attributes and json,
 *  if the serialization method is the same.
 * </p>
 * 
 * @param <S>
 *  Type of source
 */
public abstract class AttributeJsonSerializer<S> implements JsonDeserializer<S>, JsonSerializer<S>, AttributeSerializer<S> {
    
    @Override
    public abstract String serialize(S source);
    
    public abstract S deserialize(String serialized);

    @Override
    public JsonElement serialize(S source, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(serialize(source));
    }

    @Override
    public S deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return deserialize(json.getAsString());
    }
}
