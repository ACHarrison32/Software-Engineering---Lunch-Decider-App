package us.four.lunchroulette;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import us.four.lunchroulette.filters.Filter;
import us.four.lunchroulette.filters.FilterFactory;
import us.four.lunchroulette.filters.Preferences;

public class FilterActivity extends AppCompatActivity {
    private Spinner spinnerFoodType, spinnerRestaurantType, spinnerPriceRange, spinnerRating, spinnerDistance, spinnerCurrentList;
    private Button createList;
    private List<Preferences> preferences = new ArrayList<>();

    public void setPreferencesList(List<Preferences> list) {
        this.preferences = list;
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
        createList = findViewById(R.id.createList);

        populateSpinnerFoodType();
        populateSpinnerRestaurantType();
        populateSpinnerPriceRange();
        populateSpinnerRating();
        populateSpinnerDistance();

        if(preferences.size() == 0) {
            System.out.println("ez");
            preferences.add(new Preferences());
        }
        populateSpinnerCurrentFilter();

        createList.setOnClickListener(view -> makeList(view));
    }

    public void makeList(View view) {
        if(((EditText) findViewById(R.id.inputText)).getText().toString() == "") {
            //you can't make a list with no name!
        }
        StringBuilder builder = new StringBuilder();
        builder.append(spinnerFoodType.getSelectedItem() + "\n");
        builder.append(spinnerRestaurantType.getSelectedItem() + "\n");
        builder.append(spinnerPriceRange.getSelectedItem() + "\n");
        builder.append(spinnerRating.getSelectedItem() + "\n");
        builder.append(spinnerDistance.getSelectedItem() + "\n");
        List<Filter> filterList = FilterFactory.generateFiltersFromString(builder.toString());
        preferences.add(new Preferences(filterList, ((EditText) findViewById(R.id.inputText)).getText().toString()));
        populateSpinnerCurrentFilter();
        for(Preferences p : this.preferences) {
            System.out.println(p.getName() + ":" + p.getDistance() + ":" + p.getFoodType() + ":" + p.getRestaurantType() + ":" + p.getPriceRange() + ":" + p.getRating());
        }
    }

    private void populateSpinnerCurrentFilter() {
        String prefsNames = "";
        for(Preferences p : this.preferences) {
            prefsNames = prefsNames + p.getName() + "\n";
        }
        prefsNames = prefsNames.trim();
        String[] filtersLists = prefsNames.split("\n");
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