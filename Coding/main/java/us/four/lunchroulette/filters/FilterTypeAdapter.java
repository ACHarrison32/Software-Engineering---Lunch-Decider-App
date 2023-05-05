package us.four.lunchroulette.filters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

// Define a public class named FilterTypeAdapter that implements the
// JsonSerializer and JsonDeserializer interfaces for the Filter class
public class FilterTypeAdapter implements JsonSerializer<Filter>, JsonDeserializer<Filter>
{
    // serialize method from the JsonSerializer interface to serialize a Filter object to JSON
    @Override
    public JsonElement serialize(Filter src, Type typeOfSrc, JsonSerializationContext context)
    {
        // Serialize MyInterface object
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", src.getClass().getName());
        jsonObject.add("data", context.serialize(src));
        return jsonObject;
    }
    // deserialize method from the JsonDeserializer interface to deserialize a Filter object from JSON
    @Override
    public Filter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        // Parse the JSON object and get the class name and data properties
        JsonObject jsonObject = json.getAsJsonObject();
        String className = jsonObject.get("type").getAsString();
        JsonElement data = jsonObject.get("data");
        try
        {
            // Deserialize the object using the specified class name
            Class<?> clazz = Class.forName(className);
            return context.deserialize(data, clazz);
        }
        catch (ClassNotFoundException e)
        {
            throw new JsonParseException(e.getMessage());
        }
    }
}
