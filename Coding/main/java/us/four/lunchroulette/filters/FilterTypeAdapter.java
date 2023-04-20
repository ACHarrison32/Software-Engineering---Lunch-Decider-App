package us.four.lunchroulette.filters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class FilterTypeAdapter implements JsonSerializer<Filter>, JsonDeserializer<Filter> {

    @Override
    public JsonElement serialize(Filter src, Type typeOfSrc, JsonSerializationContext context) {
        // Serialize MyInterface object
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", src.getClass().getName());
        jsonObject.add("data", context.serialize(src));
        return jsonObject;
    }

    @Override
    public Filter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String className = jsonObject.get("type").getAsString();
        JsonElement data = jsonObject.get("data");
        try {
            // Deserialize the object using the specified class name
            Class<?> clazz = Class.forName(className);
            return context.deserialize(data, clazz);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e.getMessage());
        }
    }
}
