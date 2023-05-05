package us.four.lunchroulette;

import android.content.Context;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yelp.fusion.client.models.Business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import us.four.lunchroulette.filters.Filter;
import us.four.lunchroulette.filters.FilterTypeAdapter;
import us.four.lunchroulette.filters.Preferences;


// This is a class definition for a file manager
public class FileManager
{
    // A static field to keep track of the current filter index
    public static int currentFilterIndex = 0;

    // A method to write preferences to a file
    public void writePrefsToFile(Context c, List<Preferences> prefs) throws IOException
    {
        // Write serialized preferences to file with name "filters.conf"
        this.writeToFile(this.serializePrefsToJson(prefs),"filters.conf", c);
    }

    // A method to write favorites to a file
    public void writeFavoritesToFile(Context c, Set<Business> prefs) throws IOException
    {
        // Write serialized favorites to file with name "favorites.conf"
        this.writeToFile(this.serializeFavoritesToJson(prefs),"favorites.conf", c);
    }

    // A method to read preferences from a file
    public List<Preferences> readPrefsFromFile(Context context) throws IOException
    {
        // Read the contents of the "filters.conf" file and deserialize to a list of preferences
        File file = new File(context.getFilesDir(), "filters.conf");
        String s = Files.asCharSource(file, StandardCharsets.UTF_8).read();
        return this.deserializePrefsFromJson(s);
    }

    // A method to read favorites from a file
    public Set<Business> readFavoritesFromFile(Context context) throws IOException
    {
        // Read the contents of the "favorites.conf" file and deserialize to a set of businesses
        File file = new File(context.getFilesDir(), "favorites.conf");
        String s = Files.asCharSource(file, StandardCharsets.UTF_8).read();
        return this.deserializeFavoritesFromJson(s);
    }

    // A private method to write data to a file
    private void writeToFile(String data, String name, Context context) throws IOException
    {
        // Write the data to a file with the given name in the context's files directory
        File file = new File(context.getFilesDir(), name);
        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(data.getBytes());
        }
    }

    // A private method to serialize a list of preferences to JSON
    private String serializePrefsToJson(List<Preferences> prefs)
    {
        // Use a Gson instance with a custom type adapter for the Filter class to serialize the preferences
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Filter.class, new FilterTypeAdapter())
                .create();

        return gson.toJson(prefs);
    }

    // A private method to serialize a set of businesses to JSON
    private String serializeFavoritesToJson(Set<Business> prefs)
    {
        // Use a Gson instance to serialize the favorites
        Gson gson = new Gson();
        return gson.toJson(prefs);
    }

    // A public method to deserialize to single object
    public List<Preferences> deserializePrefsFromJson(String jsonString)
    {
        // Use a Gson instance with a custom type adapter for the Filter class to deserialize the preferences
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Filter.class, new FilterTypeAdapter())
                .create();
        // Use TypeToken to get the type of the List of Preferences and deserialize the JSON string
        Type typeOfT = TypeToken.getParameterized(List.class, Preferences.class).getType();
        return gson.fromJson(jsonString, typeOfT);
    }

    // A public method to deserialize a JSON string to a set of businesses
    public Set<Business> deserializeFavoritesFromJson(String jsonString)
    {
        // Use a Gson instance to deserialize the favorites
        Gson gson = new Gson();
        // Use TypeToken to get the type of the Set of Business and deserialize the JSON string
        Type typeOfT = TypeToken.getParameterized(Set.class, Business.class).getType();
        return gson.fromJson(jsonString, typeOfT);
    }
}
