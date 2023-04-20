package us.four.lunchroulette;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.myapplication.R;

public class FilterActivity extends AppCompatActivity {
    Spinner spinnerFoodType, spinnerRestaurantType, spinnerPriceRange, spinnerRating, spinnerDistance;
    Button createList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        spinnerFoodType = findViewById(R.id.spinnerFoodType);
        spinnerRestaurantType = findViewById(R.id.spinnerRestaurantType);
        spinnerPriceRange = findViewById(R.id.spinnerPriceRange);
        spinnerRating = findViewById(R.id.spinnerRating);
        spinnerDistance = findViewById(R.id.spinnerDistance);
        createList = findViewById(R.id.createList);

        populateSpinnerFoodType();
        populateSpinnerRestaurantType();
        populateSpinnerPriceRange();
        populateSpinnerRating();
        populateSpinnerDistance();

        createList.setOnClickListener(view -> makeList(view));
    }

    public void makeList(View view) {
        StringBuilder builder = new StringBuilder();
        builder.append(spinnerFoodType.getSelectedItem() + "\n");
        builder.append(spinnerRestaurantType.getSelectedItem() + "\n");
        builder.append(spinnerPriceRange.getSelectedItem() + "\n");
        builder.append(spinnerRating.getSelectedItem() + "\n");
        builder.append(spinnerDistance.getSelectedItem() + "\n");
        System.out.println(builder);

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