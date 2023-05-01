package us.four.lunchroulette;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import us.four.lunchroulette.filters.Filter;
import us.four.lunchroulette.filters.FilterFactory;
import us.four.lunchroulette.filters.FoodType;
import us.four.lunchroulette.filters.Preferences;
import us.four.lunchroulette.filters.RestaurantType;

public class FilterActivity extends AppCompatActivity {
    private Spinner spinnerFoodType, spinnerRestaurantType, spinnerPriceRange, spinnerRating, spinnerDistance, spinnerCurrentList;
    private List<Preferences> preferences = new ArrayList<>();

    public void setPreferencesList(List<Preferences> list) {
        this.preferences = list;
    }

    public List<Preferences> getPreferencesList() {
        return preferences;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        spinnerFoodType = findViewById(R.id.spinnerFoodType);
        spinnerRestaurantType = findViewById(R.id.spinnerRestaurantType);
        spinnerPriceRange = findViewById(R.id.spinnerPriceRange);
        spinnerRating = findViewById(R.id.spinnerRating);
        spinnerDistance = findViewById(R.id.spinnerDistance);
        spinnerCurrentList = findViewById(R.id.currentFilterSpinner);
        Button createList = findViewById(R.id.createList);
        Button deleteFilter = findViewById(R.id.deleteFilter);

        populateSpinnerFoodType();
        populateSpinnerRestaurantType();
        populateSpinnerPriceRange();
        populateSpinnerRating();
        populateSpinnerDistance();
        FileManager fm = new FileManager();
        try {
            this.setPreferencesList(fm.readPrefsFromFile(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (preferences.size() == 0) {
            preferences.add(new Preferences());
        }
        populateSpinnerCurrentFilter();

        createList.setOnClickListener(this::makeList);
        deleteFilter.setOnClickListener(this::deleteFilter);

        spinnerCurrentList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FileManager.currentFilterIndex = spinnerCurrentList.getSelectedItemPosition();
                System.out.println(FileManager.currentFilterIndex);
                EditText etext = findViewById(R.id.inputText);
                Preferences filter = getPreferencesList().get(spinnerCurrentList.getSelectedItemPosition());
                if(filter.getName().equals("Any"))
                    etext.setText("");
                else
                    etext.setText(filter.getName());
                Spinner spinner = spinnerFoodType;
                int i = 0;
                for(FoodType f : FoodType.values()) {
                    if(f == filter.getFoodType()) {
                        spinner.setSelection(i);
                    }
                    i++;
                }
                spinner = spinnerRestaurantType;
                i = 0;
                for(RestaurantType r : RestaurantType.values()) {
                    if(r == filter.getRestaurantType()) {
                        spinner.setSelection(i);
                    }
                    i++;
                }
                spinner = spinnerPriceRange;
                spinner.setSelection(filter.getPriceRange());
                spinner = spinnerRating;
                spinner.setSelection(filter.getRating());
                spinner = spinnerDistance;
                int[] distances = {0, 1, 5, 10, 15, 20, 25, 50};
                for(int d = 0; d < distances.length; d++) {
                    if(filter.getDistance() == distances[d]) {
                        spinner.setSelection(d);
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerCurrentList.setSelection(FileManager.currentFilterIndex);
        System.out.println(FileManager.currentFilterIndex);
    }

    public void deleteFilter(View view) {
        int pos = spinnerCurrentList.getSelectedItemPosition();
        if(pos > 0) {
            this.preferences.remove(pos);
            populateSpinnerCurrentFilter();
            FileManager fm = new FileManager();
            try {
                fm.writePrefsToFile(this, preferences);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //spinnerCurrentList.getOnItemSelectedListener().onItemSelected();
        }

    }

    public void makeList(View view) {
        if(((EditText) findViewById(R.id.inputText)).getText().toString().equals("")) {
            Toast.makeText(this, "Your list must have a name!", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(spinnerFoodType.getSelectedItem()).append("\n");
        builder.append(spinnerRestaurantType.getSelectedItem()).append("\n");
        builder.append(spinnerPriceRange.getSelectedItem()).append("\n");
        builder.append(spinnerRating.getSelectedItem()).append("\n");
        if(spinnerDistance.getSelectedItem().toString().split(" ").length <= 1) {
            builder.append("Any");
        } else
            builder.append(spinnerDistance.getSelectedItem().toString().split(" ")[2]);
        List<Filter> filterList = FilterFactory.generateFiltersFromString(builder.toString());
        int existingIndex = 0;
        int i = 0;
        boolean alreadyExists = false;
        for(Preferences p : preferences) {
            if(p.getName().equals(((EditText) findViewById(R.id.inputText)).getText().toString())) {
                preferences.set(i, new Preferences(filterList, ((EditText) findViewById(R.id.inputText)).getText().toString()));
                alreadyExists = true;
                existingIndex = i;
            }
            i++;
        }
        if(!alreadyExists)
            preferences.add(new Preferences(filterList, ((EditText) findViewById(R.id.inputText)).getText().toString()));
        populateSpinnerCurrentFilter();
        FileManager fm = new FileManager();
        try {
            fm.writePrefsToFile(this, preferences);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(existingIndex != 0) {
            spinnerCurrentList.setSelection(existingIndex);
        } else
            spinnerCurrentList.setSelection(preferences.size()-1);
        FileManager.currentFilterIndex = spinnerCurrentList.getSelectedItemPosition();
    }

    private void populateSpinnerCurrentFilter() {
        StringBuilder prefsNames = new StringBuilder();
        for(Preferences p : this.preferences) {
            prefsNames.append(p.getName()).append("\n");
        }
        prefsNames = new StringBuilder(prefsNames.toString().trim());
        String[] filtersLists = prefsNames.toString().split("\n");
        ArrayAdapter<String> currentListAdapter = new ArrayAdapter<>(this, R.layout.my_selected_item, filtersLists);
        currentListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrentList.setAdapter(currentListAdapter);
    }
    private void populateSpinnerDistance() {
        ArrayAdapter<String> distanceAdapter = new ArrayAdapter<>(this, R.layout.my_selected_item, getResources().getStringArray(R.array.spinner_distance));
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistance.setAdapter(distanceAdapter);
    }

    private void populateSpinnerRating() {
        ArrayAdapter<String> ratingAdapter = new ArrayAdapter<>(this, R.layout.my_selected_item, getResources().getStringArray(R.array.spinner_rating));
        ratingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRating.setAdapter(ratingAdapter);
    }

    private void populateSpinnerPriceRange() {
        ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(this, R.layout.my_selected_item, getResources().getStringArray(R.array.spinner_price_range));
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriceRange.setAdapter(priceAdapter);
    }

    private void populateSpinnerRestaurantType() {
        ArrayAdapter<String> restaurantAdapter = new ArrayAdapter<>(this, R.layout.my_selected_item, getResources().getStringArray(R.array.spinner_restaurant_type));
        restaurantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRestaurantType.setAdapter(restaurantAdapter);
    }

    private void populateSpinnerFoodType() {
        ArrayAdapter<String> foodAdapter = new ArrayAdapter<>(this, R.layout.my_selected_item, getResources().getStringArray(R.array.spinner_food_type));
        foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFoodType.setAdapter(foodAdapter);
    }
}