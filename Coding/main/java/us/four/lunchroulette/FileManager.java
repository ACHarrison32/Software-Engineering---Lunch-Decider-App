package us.four.lunchroulette;

import android.content.Context;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

import us.four.lunchroulette.filters.Filter;
import us.four.lunchroulette.filters.FilterTypeAdapter;
import us.four.lunchroulette.filters.Preferences;

public class FileManager {
    public static int currentFilterIndex = 0;
    public void writePrefsToFile(Context c, List<Preferences> prefs) throws IOException {
        this.writeToFile(this.serializeToJson(prefs), c);
    }

    public List<Preferences> readPrefsFromFile(Context context) throws IOException {
        File file = new File(context.getFilesDir(), "filters.conf");
        String s = Files.asCharSource(file, StandardCharsets.UTF_8).read();
        return this.deserializeFromJson(s);
    }

    private void writeToFile(String data, Context context) throws IOException {
        File file = new File(context.getFilesDir(), "filters.conf");
        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(data.getBytes());
        }
    }

    private String serializeToJson(List<Preferences> prefs) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Filter.class, new FilterTypeAdapter())
                .create();

        return gson.toJson(prefs);
    }
    // Deserialize to single object.
    public List<Preferences> deserializeFromJson(String jsonString) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Filter.class, new FilterTypeAdapter())
                .create();
        Type typeOfT = TypeToken.getParameterized(List.class, Preferences.class).getType();
        return gson.fromJson(jsonString, typeOfT);
    }
}
