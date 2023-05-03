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

public class FileManager {
    public static int currentFilterIndex = 0;
    public void writePrefsToFile(Context c, List<Preferences> prefs) throws IOException {
        this.writeToFile(this.serializePrefsToJson(prefs),"filters.conf", c);
    }
    public void writeFavoritesToFile(Context c, Set<Business> prefs) throws IOException {
        this.writeToFile(this.serializeFavoritesToJson(prefs),"favorites.conf", c);
    }

    public List<Preferences> readPrefsFromFile(Context context) throws IOException {
        File file = new File(context.getFilesDir(), "filters.conf");
        String s = Files.asCharSource(file, StandardCharsets.UTF_8).read();
        return this.deserializePrefsFromJson(s);
    }
    public Set<Business> readFavoritesFromFile(Context context) throws IOException {
        File file = new File(context.getFilesDir(), "favorites.conf");
        String s = Files.asCharSource(file, StandardCharsets.UTF_8).read();
        return this.deserializeFavoritesFromJson(s);
    }

    private void writeToFile(String data, String name, Context context) throws IOException {
        File file = new File(context.getFilesDir(), name);
        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(data.getBytes());
        }
    }

    private String serializePrefsToJson(List<Preferences> prefs) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Filter.class, new FilterTypeAdapter())
                .create();

        return gson.toJson(prefs);
    }
    private String serializeFavoritesToJson(Set<Business> prefs) {
        Gson gson = new Gson();
        return gson.toJson(prefs);
    }
    // Deserialize to single object.
    public List<Preferences> deserializePrefsFromJson(String jsonString) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Filter.class, new FilterTypeAdapter())
                .create();
        Type typeOfT = TypeToken.getParameterized(List.class, Preferences.class).getType();
        return gson.fromJson(jsonString, typeOfT);
    }
    public Set<Business> deserializeFavoritesFromJson(String jsonString) {
        Gson gson = new Gson();
        Type typeOfT = TypeToken.getParameterized(Set.class, Business.class).getType();
        return gson.fromJson(jsonString, typeOfT);
    }
}
