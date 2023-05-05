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

// This is the class definition called FilterActivity
public class FilterActivity extends AppCompatActivity
{
    // Declare variables for all Spinners
    private Spinner spinnerFoodType, spinnerRestaurantType, spinnerPriceRange, spinnerRating, spinnerDistance, spinnerCurrentList;
    // Declare variable for List of Preferences
    private List<Preferences> preferences = new ArrayList<>();

    // Setter method for the List of Preferences
    public void setPreferencesList(List<Preferences> list) {
        this.preferences = list;
    }

    // Getter method for the List of Preferences
    public List<Preferences> getPreferencesList() {
        return preferences;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Set the layout to be displayed
        setContentView(R.layout.activity_filter);

        // Find the spinners and buttons in the layout
        spinnerFoodType = findViewById(R.id.spinnerFoodType);
        spinnerRestaurantType = findViewById(R.id.spinnerRestaurantType);
        spinnerPriceRange = findViewById(R.id.spinnerPriceRange);
        spinnerRating = findViewById(R.id.spinnerRating);
        spinnerDistance = findViewById(R.id.spinnerDistance);
        spinnerCurrentList = findViewById(R.id.currentFilterSpinner);
        Button createList = findViewById(R.id.createList);
        Button deleteFilter = findViewById(R.id.deleteFilter);

        // Populate the spinners with their respective data
        populateSpinnerFoodType();
        populateSpinnerRestaurantType();
        populateSpinnerPriceRange();
        populateSpinnerRating();
        populateSpinnerDistance();

        // Read user preferences from file and set them as the current preferences
        FileManager fm = new FileManager();
        try {
            this.setPreferencesList(fm.readPrefsFromFile(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // If no preferences were read from the file, add a default preferences and a favorites preferences
        if (preferences.size() == 0) {
            preferences.add(new Preferences());
            Preferences favesPref = new Preferences();
            favesPref.setName("Favorites");
            preferences.add(favesPref);
        }

        // Populate the spinner for selecting the current filter with the available preferences
        populateSpinnerCurrentFilter();

        // Set listeners for the create list and delete filter buttons
        createList.setOnClickListener(this::makeList);
        deleteFilter.setOnClickListener(this::deleteFilter);

        // Set a listener for when an item is selected in the spinner for the current filter
        spinnerCurrentList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                // Update the current filter index in the FileManager
                FileManager.currentFilterIndex = spinnerCurrentList.getSelectedItemPosition();
                System.out.println(FileManager.currentFilterIndex);

                // Set the input text field to display the name of the selected filter
                EditText etext = findViewById(R.id.inputText);
                Preferences filter = getPreferencesList().get(spinnerCurrentList.getSelectedItemPosition());
                if(filter.getName().equals("Any"))
                    etext.setText("");
                else
                    etext.setText(filter.getName());

                // Set the spinner for food type to their corresponding values in the selected filter
                Spinner spinner = spinnerFoodType;
                int i = 0;
                for(FoodType f : FoodType.values()) {
                    if(f == filter.getFoodType()) {
                        spinner.setSelection(i);
                    }
                    i++;
                }

                // Set the spinner for restaurant type to their corresponding values in the selected filter
                spinner = spinnerRestaurantType;
                i = 0;
                for(RestaurantType r : RestaurantType.values()) {
                    if(r == filter.getRestaurantType()) {
                        spinner.setSelection(i);
                    }
                    i++;
                }

                // Set the spinner for price range to their corresponding values in the selected filter
                spinner = spinnerPriceRange;
                spinner.setSelection(filter.getPriceRange());

                // Set the spinner for rating to their corresponding values in the selected filter
                spinner = spinnerRating;
                spinner.setSelection(filter.getRating());

                // Set the spinner for distance to their corresponding values in the selected filter
                spinner = spinnerDistance;
                int[] distances = {0, 1, 5, 10, 15, 20};
                if(filter.getFilters().get(4).getValue().equals("Any"))
                    spinner.setSelection(0);
                else {
                    for (int d = 0; d < distances.length; d++) {
                        if (filter.getDistance() == (int) (distances[d] * 1609.344)) {
                            spinner.setSelection(d);
                        }
                    }
                }
            }

            // Set a listener for when nothing is selected in the spinner for the current filter
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        // Set the spinner for the current filter to display the filter at the current filter index in the FileManager
        spinnerCurrentList.setSelection(FileManager.currentFilterIndex);
        System.out.println(FileManager.currentFilterIndex);
    }

    //method that is called when the "delete filter" button is clicked
    public void deleteFilter(View view)
    {
        // Get the position of the selected item in the spinner for the current filter
        int pos = spinnerCurrentList.getSelectedItemPosition();
        // Check if the position is greater than 1 (i.e., if the filter being deleted is not the default or "Any" filter)
        if(pos > 1)
        {
            // Remove the filter at the selected position from the list of preferences
            this.preferences.remove(pos);
            // Update the spinner for the current filter to reflect the change
            populateSpinnerCurrentFilter();
            // Write the updated preferences to file
            FileManager fm = new FileManager();
            try {
                fm.writePrefsToFile(this, preferences);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //spinnerCurrentList.getOnItemSelectedListener().onItemSelected();
        }

    }

    //method that is called when the "create list" button is clicked
    public void makeList(View view)
    {
        // Check if the list has a name
        if(((EditText) findViewById(R.id.inputText)).getText().toString().equals(""))
        {
            // Display error message if user fails to enter name
            Toast.makeText(this, "Your list must have a name!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if the list name is "Favorites"
        if(((EditText) findViewById(R.id.inputText)).getText().toString().equals("Favorites"))
        {
            // Display error message if user tries to overwrite "favorites" list name
            Toast.makeText(this, "You Can't Overwrite Favorites!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build a string of selected filter options
        StringBuilder builder = new StringBuilder();
        builder.append(spinnerFoodType.getSelectedItem()).append("\n");
        builder.append(spinnerRestaurantType.getSelectedItem()).append("\n");
        builder.append(spinnerPriceRange.getSelectedItem()).append("\n");
        builder.append(spinnerRating.getSelectedItem()).append("\n");

        // Check if the distance spinner selection is less than or equal to 1, if so, add "Any" to the builder.
        if(spinnerDistance.getSelectedItem().toString().split(" ").length <= 1) {
            builder.append("Any");
        }
        // Otherwise, add the distance value to the builder.
        else
            builder.append(spinnerDistance.getSelectedItem().toString().split(" ")[2]);

        // Generate filters from the selected options
        List<Filter> filterList = FilterFactory.generateFiltersFromString(builder.toString());
        int existingIndex = 0;
        int i = 0;
        boolean alreadyExists = false;
        // Check if the list already exists, update it if it does, or add it if it doesn't
        for(Preferences p : preferences) {
            if(p.getName().equals(((EditText) findViewById(R.id.inputText)).getText().toString())) {
                preferences.set(i, new Preferences(filterList, ((EditText) findViewById(R.id.inputText)).getText().toString()));
                alreadyExists = true;
                existingIndex = i;
            }
            i++;
        }
        // If the list doesn't already exist, create a new one
        if(!alreadyExists)
            preferences.add(new Preferences(filterList, ((EditText) findViewById(R.id.inputText)).getText().toString()));
        // Populate the spinner with the updated filter list
        populateSpinnerCurrentFilter();
        // Write the updated preferences to file
        FileManager fm = new FileManager();
        try {
            fm.writePrefsToFile(this, preferences);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Reset the spinner selection and current filter index
        spinnerCurrentList.setSelection(0);
        FileManager.currentFilterIndex = spinnerCurrentList.getSelectedItemPosition();
    }

    // Populates the "Current List" spinner with the names of the available preferences
    private void populateSpinnerCurrentFilter()
    {
        // Build a string with the name of each preference
        StringBuilder prefsNames = new StringBuilder();
        for(Preferences p : this.preferences) {
            prefsNames.append(p.getName()).append("\n");
        }
        // Trim the StringBuilder object
        prefsNames = new StringBuilder(prefsNames.toString().trim());
        // Split the string into an array of strings, one for each preference name
        String[] filtersLists = prefsNames.toString().split("\n");
        // Create an adapter for the spinner, using the array of preference names
        ArrayAdapter<String> currentListAdapter = new ArrayAdapter<>(this, R.layout.my_selected_item, filtersLists);
        currentListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set the adapter for the spinner
        spinnerCurrentList.setAdapter(currentListAdapter);
    }

    // Populates the Spinner with the distance options
    private void populateSpinnerDistance() {
        // Creates an ArrayAdapter with the string array and a customized layout
        ArrayAdapter<String> distanceAdapter = new ArrayAdapter<>(this, R.layout.my_selected_item, getResources().getStringArray(R.array.spinner_distance));
        // Sets the dropdown layout to a default layout provided by the system
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Sets the adapter to the Spinner
        spinnerDistance.setAdapter(distanceAdapter);
    }

    // Populates the Spinner with the rating options
    private void populateSpinnerRating() {
        // Creates an ArrayAdapter with the string array and a customized layout
        ArrayAdapter<String> ratingAdapter = new ArrayAdapter<>(this, R.layout.my_selected_item, getResources().getStringArray(R.array.spinner_rating));
        // Sets the dropdown layout to a default layout provided by the system
        ratingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Sets the adapter to the Spinner
        spinnerRating.setAdapter(ratingAdapter);
    }

    // Populates the Spinner with the price range options
    private void populateSpinnerPriceRange() {
        // Creates an ArrayAdapter with the string array and a customized layout
        ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(this, R.layout.my_selected_item, getResources().getStringArray(R.array.spinner_price_range));
        // Sets the dropdown layout to a default layout provided by the system
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Sets the adapter to the Spinner
        spinnerPriceRange.setAdapter(priceAdapter);
    }

    // Populates the Spinner with the restaurant type options
    private void populateSpinnerRestaurantType() {
        // Creates an ArrayAdapter with the string array and a customized layout
        ArrayAdapter<String> restaurantAdapter = new ArrayAdapter<>(this, R.layout.my_selected_item, getResources().getStringArray(R.array.spinner_restaurant_type));
        // Sets the dropdown layout to a default layout provided by the system
        restaurantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Sets the adapter to the Spinner
        spinnerRestaurantType.setAdapter(restaurantAdapter);
    }

    // Populates the Spinner with the food type options
    private void populateSpinnerFoodType() {
        // Creates an ArrayAdapter with the string array and a customized layout
        ArrayAdapter<String> foodAdapter = new ArrayAdapter<>(this, R.layout.my_selected_item, getResources().getStringArray(R.array.spinner_food_type));
        // Sets the dropdown layout to a default layout provided by the system
        foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Sets the adapter to the Spinner
        spinnerFoodType.setAdapter(foodAdapter);
    }
}