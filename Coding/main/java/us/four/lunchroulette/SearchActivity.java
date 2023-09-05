package us.four.lunchroulette;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.four.lunchroulette.filters.Preferences;

// Define a public class named SearchActivity that extends AppCompatActivity class
public class SearchActivity extends AppCompatActivity {
    //onCreate() method to create and initialize the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Set the OnClickListener of the searchButton to the findRestaurants method
        findViewById(R.id.searchButton).setOnClickListener(this::findRestaurants);
        this.getSupportActionBar().setTitle("Search");
    }

    // This method retrieves search results from Yelp Fusion API using parameters in a HashMap
    public void getResults(HashMap<String, String> params, View view)
    {
        // Create a new instance of YelpFusionApiFactory
        YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
        // Get a reference to the current Activity and assign it to a final variable called c
        final Activity c = this;
        try {
            //This API key is limited in requests per day, but scales in load if we get more users and contact yelp
            YelpFusionApi yelpFusionApi = apiFactory.createAPI("WWc44gE8YXQor0rQC5cuPTmh1R6Bq6fhqMxJXDqxoRlefB-NjmNyOgVjggoq4E7NQ-g5grrk_rYewxMATnO_DkGIfrtfzohzxEL3FfoBZXLREfjnOG4JZGuMDlM0ZHYx");
            // Retrieves the business search results based on the given parameters
            Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
            // Callback function that handles the response from the API
            Callback<SearchResponse> callback = new Callback<SearchResponse>() {
                // If the response was successful, handle it in onResponse
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    // Get the search response from the response body
                    SearchResponse searchResponse = response.body();
                    // Iterate over the list of businesses returned in the response and create a button for each one
                    for(Business b : searchResponse.getBusinesses()) {
                        // Get a reference to the button container LinearLayout
                        LinearLayout buttonContainer = (LinearLayout) findViewById(R.id.buttonGroup);
                        // Create a new button and set its text to the business name
                        Button button = new Button(c);
                            button.setText(b.getName());
                            button.setTextSize(12);
                            // Set an OnClickListener for the button that deploys a popup for the selected busines
                            button.setOnClickListener(e -> {
                                Executor executor = command -> new Thread(command).start();
                                // Execute the popup deployment code in the Executor
                                executor.execute(() -> {
                                    // Get the image for the business from the URL and assign it to a variable called image
                                    Drawable image = MainActivity.INSTANCE.getImageFromUrl(b.getImageUrl());
                                    // Run the popup deployment code on the main UI thread
                                    c.runOnUiThread(() -> deployPopup(view, b, image));
                                });
                            });
                            // Add the button to the button container on the main UI thread
                            c.runOnUiThread(() -> buttonContainer.addView(button));
                        }
                }
                // If the response failed, handle it in onFailure
                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    // HTTP error happened, do something to handle it.
                }
            };
            // Enqueue the call to the Yelp Fusion API with the defined callback
            call.enqueue(callback);
        } catch (Exception e) {
            // Print the stack trace if there is an exception
            e.printStackTrace();
        }
    }

    // A public method that takes in a view object as a parameter
    public void findRestaurants(View view) {
        // Get the EditText view object by its ID
        EditText text = findViewById(R.id.editTextName);
        // Check if the text field is empty
        if(text.getText().toString().length() == 0) {
            // Display an error message if user fails to enter restaurant name
            Toast.makeText(this, "You must enter a restaurant name!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create a new HashMap object and set some parameters
        HashMap<String, String> params = new HashMap<>();
        params.put("radius", "32186");
        params.put("sort_by", "best_match");
        params.put("categories", "restaurants");
        params.put("term", text.getText().toString());
        params.put("latitude", MainActivity.INSTANCE.gpsTracker.getLatitude() + "");
        params.put("longitude", MainActivity.INSTANCE.gpsTracker.getLongitude() + "");
        params.put("limit", "12");
        // Get the LinearLayout object by its ID
        LinearLayout buttonContainer = (LinearLayout) findViewById(R.id.buttonGroup);
        // Remove all views from the LinearLayout
        buttonContainer.removeAllViews();
        // Call a method to get the results from Yelp Fusion API by passing in the HashMap and the view object
        getResults(params, view);
    }

    // define a method that deploys a popup window displaying information about a restaurant
    public void deployPopup(View view, Business restaurant, Drawable img) {
        //search list for restaurant name
        Business business = restaurant;

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_restaurant_info, null);



        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // get the background color of the activity
        int color = 0xFFFFFFFF;
        TypedValue typedValue = new TypedValue();
        if (this.getTheme().resolveAttribute(android.R.attr.windowBackground, typedValue, true))
        {
            color = typedValue.data;
        }
        //set it as the background color of the popup window
        popupWindow.setBackgroundDrawable(new ColorDrawable(color));
        popupWindow.setElevation(20);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        // dismiss the popup window when touched
        popupView.setOnTouchListener((v, event) -> {
            view.performClick();
            popupWindow.dismiss();
            return true;
        });

        // Find the "reroll" button view in the popup layout
        Button reroll = popupView.findViewById(R.id.rerollButton);
        // Set the text of the "reroll" button to "Close"
        reroll.setText("Close");
        // Set an onClickListener for the "reroll" button to dismiss the popup window when clicked
        reroll.setOnClickListener(v -> popupWindow.dismiss());
        // Find the "favSwitch" switch view in the popup layout
        Switch favSwitch = popupView.findViewById(R.id.favSwitch);
        Context c = this;
        // Set an onCheckedChangeListener for the "favSwitch" switch to add or remove a restaurant
        // from the favorites list
        favSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean found = false;
            Business deleteB = null;
            // Check if the restaurant is already in the favorites list
            for(Business x : MainActivity.INSTANCE.favorites) {
                if(x.getLocation().getAddress1().equals(business.getLocation().getAddress1())) {
                    found = true;
                    deleteB = x;
                }
            }
            // Add or remove the restaurant from the favorites list depending on the switch's state
            if(isChecked) {
                if(!found)
                    MainActivity.INSTANCE.favorites.add(business);
            } else {
                if(found)
                    MainActivity.INSTANCE.favorites.remove(deleteB);
            }
            // Write the favorites list to a file
            FileManager manager = new FileManager();
            try {
                manager.writeFavoritesToFile(c, MainActivity.INSTANCE.favorites);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // If the current filter index is 1, run a sequence of commands asynchronously
            if(FileManager.currentFilterIndex == 1) {
                // create an instance of Executor functional interface
                Executor executor = command -> new Thread(command).start();
                // execute a Runnable asynchronously on a new thread
                executor.execute(() -> {
                    try {
                        // sleep the thread for 1000 milliseconds
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // throw a RuntimeException if the thread is interrupted
                        throw new RuntimeException(e);
                    }
                    // set the value of currentFilterIndex to 0
                    FileManager.currentFilterIndex = 0;
                    try {
                        // sleep the thread for 500 milliseconds
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // throw a RuntimeException if the thread is interrupted
                        throw new RuntimeException(e);
                    }
                    // set the value of currentFilterIndex to 1
                    FileManager.currentFilterIndex = 1;
                });
            }
        });
        // Find the "navigate" button in the popup view and set a click listener
        Button navigate = popupView.findViewById(R.id.navButton);
        navigate.setOnClickListener(v -> {
            assert business != null;
            // Create an intent to open a map app with the restaurant location as the search query
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=" + business.getLocation().getAddress1()));
            // Start the intent
            startActivity(intent);
        });
        //and then call the click listener manually LOL
        //this is really dumb and a huge hack
        //but as far as i can tell android forces you to do this
        //if you want to pass info to a PopupWindow
        //get some random object within it
        ImageView b = popupView.findViewById(R.id.restaurantImage);
        //give it a click listener where you write your code
        b.setOnClickListener(v -> {
            // Read the list of favorite restaurants from a file
            FileManager manager = new FileManager();
            boolean found = false;
            try {
                MainActivity.INSTANCE.favorites = manager.readFavoritesFromFile(c);
                // Check if the current restaurant is in the list of favorites
                for(Business x : MainActivity.INSTANCE.favorites) {
                    if(x.getLocation().getAddress1().equals(business.getLocation().getAddress1())) {
                        found = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // If the current restaurant is a favorite, set the "favorite" switch to checked
            if(found) {
                favSwitch.setChecked(true);
            }
            // Fill the popup window with information about the restaurant
            assert business != null;
            fillRestaurant(business, popupView, img);
        });
        // Call the click listener for the ImageView (this is a hack to automatically fill the popup window
        b.callOnClick();


    }
    @SuppressLint("SetTextI18n")
    private void fillRestaurant(Business b, View view, Drawable img) {
        //get instances of TextViews and ImageView
        TextView name = view.findViewById(R.id.nameText);
        TextView rating = view.findViewById(R.id.ratingText);
        TextView phone = view.findViewById(R.id.phoneText);
        TextView distance = view.findViewById(R.id.distanceText);
        ImageView image = view.findViewById(R.id.restaurantImage);

        //we can't use half of a star currently :(
        int stars = (int) Math.floor(b.getRating());

        //Builds a string of stars for the rating
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < stars; i++) {
            s.append("⭐");
        }
        //append the decimal value after the emojis
        s.append(" (").append(b.getRating()).append(")");
        //the rating text
        rating.setText(s.toString());
        //set the name
        name.setText(
                Html.fromHtml("<a href=\"" + b.getUrl() + "\">" + b.getName() + "</a> "));
        name.setMovementMethod(LinkMovementMethod.getInstance());
        //set the image
        image.setImageDrawable(img);
        //set the distance, converted from meters to miles
        distance.setText(Math.round(b.getDistance() * 0.0006213712) + " Miles Away");
        //set the phone number
        phone.setText(b.getDisplayPhone());
    }
}
