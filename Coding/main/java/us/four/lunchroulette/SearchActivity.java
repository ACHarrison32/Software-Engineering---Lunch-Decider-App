package us.four.lunchroulette;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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

public class SearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViewById(R.id.searchButton).setOnClickListener(this::findRestaurants);
    }

    public void getResults(HashMap<String, String> params, View view) {
        YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
        final Activity c = this;
        try {
            //This API key is limited in requests per day, but scales in load if we get more users and contact yelp
            YelpFusionApi yelpFusionApi = apiFactory.createAPI("WWc44gE8YXQor0rQC5cuPTmh1R6Bq6fhqMxJXDqxoRlefB-NjmNyOgVjggoq4E7NQ-g5grrk_rYewxMATnO_DkGIfrtfzohzxEL3FfoBZXLREfjnOG4JZGuMDlM0ZHYx");
            Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
            Callback<SearchResponse> callback = new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    SearchResponse searchResponse = response.body();
                        for(Business b : searchResponse.getBusinesses()) {
                            LinearLayout buttonContainer = (LinearLayout) findViewById(R.id.buttonGroup);
                            Button button = new Button(c);
                            button.setText(b.getName());
                            button.setOnClickListener(e -> {
                                Executor executor = command -> new Thread(command).start();
                                executor.execute(() -> {
                                    Drawable image = MainActivity.INSTANCE.getImageFromUrl(b.getImageUrl());
                                    c.runOnUiThread(() -> deployPopup(view, b, image));
                                });
                            });
                            c.runOnUiThread(() -> buttonContainer.addView(button));
                        }
                }
                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    // HTTP error happened, do something to handle it.
                }
            };
            call.enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findRestaurants(View view) {
        EditText text = findViewById(R.id.editTextName);
        if(text.getText().toString().length() == 0) {
            Toast.makeText(this, "Your list must have a name!", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("radius", "32186");
        params.put("sort_by", "best_match");
        params.put("term", text.getText().toString());
        params.put("latitude", MainActivity.INSTANCE.gpsTracker.getLatitude() + "");
        params.put("longitude", MainActivity.INSTANCE.gpsTracker.getLongitude() + "");
        params.put("limit", "11");
        LinearLayout buttonContainer = (LinearLayout) findViewById(R.id.buttonGroup);
        buttonContainer.removeAllViews();
        getResults(params, view);
    }

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

        //TODO: COMMENT THIS
        int color = 0xFFFFFFFF;
        TypedValue typedValue = new TypedValue();
        if (this.getTheme().resolveAttribute(android.R.attr.windowBackground, typedValue, true))
        {
            color = typedValue.data;
        }

        popupWindow.setBackgroundDrawable(new ColorDrawable(color));
        popupWindow.setElevation(20);

        // popupWindow.set
        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        // dismiss the popup window when touched
        popupView.setOnTouchListener((v, event) -> {
            view.performClick();
            popupWindow.dismiss();
            return true;
        });


        Button reroll = popupView.findViewById(R.id.rerollButton);
        reroll.setText("Close");
        reroll.setOnClickListener(v -> popupWindow.dismiss());
        Switch favSwitch = popupView.findViewById(R.id.favSwitch);
        Context c = this;
        favSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean found = false;
            Business deleteB = null;
            for(Business x : MainActivity.INSTANCE.favorites) {
                if(x.getLocation().getAddress1().equals(business.getLocation().getAddress1())) {
                    found = true;
                    deleteB = x;
                }
            }

            if(isChecked) {
                if(!found)
                    MainActivity.INSTANCE.favorites.add(business);
            } else {
                if(found)
                    MainActivity.INSTANCE.favorites.remove(deleteB);
            }
            FileManager manager = new FileManager();
            try {
                manager.writeFavoritesToFile(c, MainActivity.INSTANCE.favorites);
                System.out.println("write");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Button navigate = popupView.findViewById(R.id.navButton);
        navigate.setOnClickListener(v -> {
            assert business != null;
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=" + business.getLocation().getAddress1()));
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
            FileManager manager = new FileManager();
            boolean found = false;
            try {
                MainActivity.INSTANCE.favorites = manager.readFavoritesFromFile(c);
                System.out.println("read ");
                for(Business x : MainActivity.INSTANCE.favorites) {
                    if(x.getLocation().getAddress1().equals(business.getLocation().getAddress1())) {
                        found = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(found) {
                favSwitch.setChecked(true);
            }
            assert business != null;
            fillRestaurant(business, popupView, img);
        });
        b.callOnClick();


    }
    @SuppressLint("SetTextI18n")
    private void fillRestaurant(Business b, View view, Drawable img) {
        TextView name = view.findViewById(R.id.nameText);
        TextView rating = view.findViewById(R.id.ratingText);
        TextView phone = view.findViewById(R.id.phoneText);
        TextView distance = view.findViewById(R.id.distanceText);
        ImageView image = view.findViewById(R.id.restaurantImage);
        int stars = (int) Math.floor(b.getRating());
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < stars; i++) {
            s.append("⭐");
        }
        s.append(" (").append(b.getRating()).append(")");
        rating.setText(s.toString());
        name.setText(b.getName());
        image.setImageDrawable(img);
        distance.setText(Math.round(b.getDistance() * 0.0006213712) + " Miles Away");
        phone.setText(b.getDisplayPhone());
    }
}
