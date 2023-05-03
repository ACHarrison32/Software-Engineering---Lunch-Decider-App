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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.four.lunchroulette.filters.Preferences;
import us.four.lunchroulette.filters.RestaurantType;


public class MainActivity extends AppCompatActivity {
    private String[] wheelText;
    private List<Business> restaurants = null;
    public Set<Business> favorites = null;
    public GPSTracker gpsTracker = null;
    public static MainActivity INSTANCE;
    /*
    * First function that gets called on program entry
    * you can treat this like 'int main()'
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //required by android
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup onclick listeners
        findViewById(R.id.button).setOnClickListener(this::filtersButton_Click);
        findViewById(R.id.searchForButton).setOnClickListener(this::searchForButton_Click);
        findViewById(R.id.imageView2).setOnClickListener(this::spin);

        //Access GPS from the user.
        GPSTracker tracker = new GPSTracker(this);
        //set gpsTracker public. Since we have a public instance
        //of the MainActivity, that means other parts of the app
        //can access the location without re-calling
        gpsTracker = tracker;
        //get a list of parameters
        Map<String, String> params = this.makeParameterMap();
        //add location
        params.put("latitude", tracker.getLatitude() + "");
        params.put("longitude", tracker.getLongitude() + "");
        //call yelp with those initial params.
        this.callYelp(params);
        //call our secondary thread that scans for currentFilter changes
        this.runChangedItemScanner(tracker);
        //create a local file manager and read the users favorites from file.
        FileManager manager = new FileManager();
        try {
            favorites = manager.readFavoritesFromFile(this);
        } catch (IOException e) {
            e.printStackTrace();
            favorites = new HashSet<>();
        }
        //initialize the public instance for other classes
        //There might be a better way to grab a parent activity
        //in android, but wasn't able to find it in time.
        //A static instance is perfectly acceptable though, since
        //there will only ever be one active instance.
        INSTANCE = this;
    }

    /**
     * Input: none
     * @return Map<String, String> of yelp parameters
     * Uses information from the Filters file to make a
     * list of parameters to send to yelp
     */
    public Map<String, String> makeParameterMap() {
        //create local map
        Map<String, String> params = new HashMap<>();
        //grab current user preferences
        Preferences pref = this.getCurrentPreference();
        //null checks with defaults are needed like everywhere
        if(pref != null) //grab radius from filters if its available
            params.put("radius", String.valueOf(pref.getDistance()));
        else //otherwise, default to 20 miles (yelp takes radius in meters)
            params.put("radius", "32186");

        if(pref != null) {
            if (pref.getPriceRange() != 0) {
                //we have to put every price range *up to* the one selected
                //for example if the user selects $$$
                //we want it to also show $ and $$
                String priceRange = "";
                //create the string depending on the int value
                for (int i = 1; i <= pref.getPriceRange(); i++) {
                    priceRange = priceRange + i + ", ";
                }
                //remove the trailing ', '
                priceRange = priceRange.substring(0, priceRange.length() - 2);
                //add to params
                params.put("price", priceRange);
            }
        }
        //we only show restaurants that are open
        params.put("open_now", "true");
        //Getting the category is one of the hardest parts
        //default is 'restaurants' - shows all categories
        String category = "restaurants";
        if(pref != null && pref.getFoodType() != null) {
            switch (pref.getFoodType()) {
                case AMERICAN: //To add 2 categories, use ',' delimiter
                                //This functions as (newamerican || tradamerican)
                                //and there is no way to do newamerican && tradamerican)
                    category = "newamerican,tradamerican";
                    break;
                case ITALIAN:
                    //All of these categories were pulled from yelps dev portal
                    category = "italian";
                    break;
                case MEXICAN:
                    category = "mexican,brazilian,newmexican,spanish,tex-mex";
                    break;
                case ASIAN:
                    //we current group all asian food together
                    category = "chinese,japanese,thai,vietnamese,noodles,ramen,indian";
                    break;
                case VEGETARIAN:
                    category = "vegetarian";
                    break;
                case BREAKFAST:
                    category = "breakfast_brunch";
                    break;
                case BBQ:
                    category = "bbq";
                    break;
            }
        }
        if(pref != null) {
            if (pref.getRestaurantType() == RestaurantType.BAR)
                params.put("term", "Bar");
            if (pref.getRestaurantType() == RestaurantType.FAST_FOOD)
                params.put("term", "Fast Food");
            if (pref.getRestaurantType() == RestaurantType.CAFE)
                params.put("term", "Cafes");
            if (pref.getRestaurantType() == RestaurantType.DINER)
                params.put("term", "Diners");
        }

        params.put("categories", category);
        params.put("sort_by", "best_match");
        return params;
    }

    private Preferences getCurrentPreference() {
        Preferences pref = null;
        FileManager fm = new FileManager();
        try {
            pref = fm.readPrefsFromFile(this).get(FileManager.currentFilterIndex);
        } catch (Exception e) {
            System.out.println("Error couldn't read prob didn't exist");
        }

        return pref;
    }

    private void setWheel(List<String> restaurantNames, Context context) {
        //Get the wheel image view
        ImageView wheelImage = findViewById(R.id.imageView2);

        //string list to appear in the wheel
        //can be up to like 10 long, only limited by how many colors you give it
        //Any more than 6 and you risk having text spacing issues
        String[] namesOfRestaurants = restaurantNames.toArray(new String[0]);
        System.out.println("changing tha restaurants! ");
        for(String s : namesOfRestaurants) {
            System.out.println(s);
        }
        wheelText = restaurantNames.toArray(new String[0]);

        //create an instance of the wheel object
        Wheel wheel = new Wheel(context, namesOfRestaurants);
        //set our image displayed to the image made by the wheel class
        wheelImage.setImageDrawable(wheel.getImage());
        //we only want to show the wheel image after its been created
        wheelImage.setVisibility(View.VISIBLE);
    }

    private void callYelp(Map<String, String> params) {

        //List of resturants that will be pulled from Yelp
        restaurants = new ArrayList<>();

        //instance of Yelp Fusion api factory
        //MIT license :)
        YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
        try {
            //This API key is limited in requests per day, but scales in load if we get more users and contact yelp
            YelpFusionApi yelpFusionApi = apiFactory.createAPI("WWc44gE8YXQor0rQC5cuPTmh1R6Bq6fhqMxJXDqxoRlefB-NjmNyOgVjggoq4E7NQ-g5grrk_rYewxMATnO_DkGIfrtfzohzxEL3FfoBZXLREfjnOG4JZGuMDlM0ZHYx");
            //call yelp api async (no networking on main thread allowed)

            //Grab current context so the concurrent part can reference it
            Context context = this;
            Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
            Callback<SearchResponse> callback = new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    SearchResponse searchResponse = response.body();
                    List<String> restaurantNames = new ArrayList<>();
                    for (Business b : searchResponse.getBusinesses()) {
                        //we only want to add about 6 resturants.
                        if (restaurants.size() < 6) {
                            if(b.getName().equals("Allsups"))
                                continue;
                            Preferences pref = getCurrentPreference();
                            if(pref != null && pref.getRating() != 0)
                                if(b.getRating() < pref.getRating()) {
                                    System.out.println("skipp because of rating!");
                                    continue;
                                }
                            restaurants.add(b);
                            restaurantNames.add(b.getName());

                        }
                    }
                    setWheel(restaurantNames, context);
                }
                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    // HTTP error happened, do something to handle it.
                }
            };
            call.enqueue(callback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runChangedItemScanner(GPSTracker tracker) {

        Executor executor = command -> new Thread(command).start();
        Activity activity = this;
        executor.execute(() -> {
            int selectedItem = FileManager.currentFilterIndex;
            while(true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(FileManager.currentFilterIndex != selectedItem) {
                        System.out.println("filter index changed!");
                        if(FileManager.currentFilterIndex == 1) {
                            System.out.println("favorites detected!");
                            FileManager manager = new FileManager();
                            try {
                                favorites = manager.readFavoritesFromFile(this);
                            } catch (IOException e) {
                                e.printStackTrace();
                                favorites = new HashSet<>();
                            }
                            for(Business b : favorites) {
                                System.out.println(b.getName() + " fave");
                            }
                            restaurants = new ArrayList<>();
                            List<String> names = new ArrayList<>();
                            for(Business favorite : this.favorites) {
                                if(restaurants.size() < 6) {
                                    restaurants.add(favorite);
                                    names.add(favorite.getName());
                                }
                            }
                            activity.runOnUiThread(() -> this.setWheel(names, activity));
                        } else {
                            Map<String, String> params = this.makeParameterMap();
                            params.put("latitude", tracker.getLatitude() + "");
                            params.put("longitude", tracker.getLongitude() + "");
//                        params.put("latitude", "33.930828");
//                        params.put("longitude", "-98.484879");
                            activity.runOnUiThread(() -> this.callYelp(params));
                        }
                    selectedItem = FileManager.currentFilterIndex;
                }
            }
        });

    }

    public void deployPopup(View view, String restaurant, Drawable img) {
        //search list for restaurant name
        Business business = null;
        for(Business b : restaurants) {
            if (b.getName().startsWith(restaurant)) {
                business = b;
            }
        }

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_restaurant_info, null);



        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //We want to get the background color appropriate for the users theme
        //fallback to white
        int color = 0xFFFFFFFF;
        TypedValue typedValue = new TypedValue();
        //grab system theme attribute
        if (this.getTheme().resolveAttribute(android.R.attr.windowBackground, typedValue, true))
        {
            //replace
            color = typedValue.data;
        }

        //set window color to the theme color we grabbed
        popupWindow.setBackgroundDrawable(new ColorDrawable(color));
        //set elevation controls how big the "shadow" is
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


        Business finalBusiness = business;
        Button reroll = popupView.findViewById(R.id.rerollButton);
        reroll.setOnClickListener(v -> popupWindow.dismiss());
        Switch favSwitch = popupView.findViewById(R.id.favSwitch);
        Context c = this;
        Activity a = this;
        favSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean found = false;
            Business deleteB = null;
            for(Business x : favorites) {
                if(x.getLocation().getAddress1().equals(finalBusiness.getLocation().getAddress1())) {
                    found = true;
                    deleteB = x;
                }
            }

            if(isChecked) {
                if(!found)
                    favorites.add(finalBusiness);
            } else {
                if(found) {
                    System.out.println("delete time");
                    favorites.remove(deleteB);
                    restaurants = new ArrayList<>();
                    List<String> names = new ArrayList<>();
                    for (Business favorite : this.favorites) {
                        if (restaurants.size() < 6) {
                            restaurants.add(favorite);
                            names.add(favorite.getName());
                        }
                    }
                    a.runOnUiThread(() -> this.setWheel(names, a));
                }
            }
            FileManager manager = new FileManager();
            try {
                manager.writeFavoritesToFile(c, favorites);
                System.out.println("write");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Button navigate = popupView.findViewById(R.id.navButton);
        navigate.setOnClickListener(v -> {
            assert finalBusiness != null;
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=" + finalBusiness.getLocation().getAddress1()));
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
                favorites = manager.readFavoritesFromFile(c);
                System.out.println("read ");
                for(Business x : favorites) {
                    if(x.getLocation().getAddress1().equals(finalBusiness.getLocation().getAddress1())) {
                        found = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(found) {
                favSwitch.setChecked(true);
            }
            assert finalBusiness != null;
            fillRestaurant(finalBusiness, popupView, img);
        });
        b.callOnClick();


    }

    /**
     * Returns a Drawable image from the provided url
     * @param url
     * @return
     */
    public Drawable getImageFromUrl(String url) {
        try {
            //get inputStream
            InputStream is = (InputStream) new URL(url).getContent();
            //use android createFromString method
            return Drawable.createFromStream(is, "src name");
        } catch (Exception e) {
            //internet failed? bad link?
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * Adds information to all textViews and ImageView
     *
     * @param Business b
     * @param view
     * @param Drawable img
     */
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
        name.setText(b.getName());
        //set the image
        image.setImageDrawable(img);
        //set the distance, converted from meters to miles
        distance.setText(Math.round(b.getDistance() * 0.0006213712) + " Miles Away");
        //set the phone number
        phone.setText(b.getDisplayPhone());
    }

    /**
     * Opens the filters intent
     * @param view
     */
    private void filtersButton_Click(View view) {
        Intent intent = new Intent("filters.intent.action.Launch");
        startActivity(intent);
    }

    /**
     * Opens the search intent
     * @param view
     */
    private void searchForButton_Click(View view) {
        Intent intent = new Intent("search.intent.action.Launch");
        startActivity(intent);
    }

    //seperate rotation int to be stored
    //helps with animation smoothness, wheel spins again from its previous rotation
    private int currentRotation = 0;
    @SuppressLint("UseCompatLoadingForDrawables")
    private void spin(View view) {



        //IMPORTANT!! WE DON'T CHOOSE A RANDOM RESTURANT FROM THE SPIN
        //INSTEAD WE CHOOSE A RANDOM ROTATION
        //FROM THERE WE USE MATH TO FIND THE RESTURANT
        //FOR EXAMPLE: WHICH RESTURANT IS 790 DEGREES OF ROTATION?


        String[] wheelRestaurantNames = wheelText;

        //Fixes a crash when tapping on a blank wheel
        if(wheelRestaurantNames.length == 0)
            return;

        //determines how much the wheel should spin
        //since the animation only takes 1000ms, more angle means a faster spin
        int rotateAmount = ((int) (Math.random() * 360) + 1080);
        //grab the imageview of our wheel
        ImageView refreshImage = this.findViewById(R.id.imageView2);
        //create a RotateAnimation
        RotateAnimation anim = new RotateAnimation(currentRotation, currentRotation + rotateAmount,Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        //save the spot of our current spin, this way its consistent if the user respins
        currentRotation = (currentRotation + rotateAmount) % 360;

        //used for getting the result
        //crazy math :)
        //first get the segment length
        int segmentLength = (360/wheelRestaurantNames.length);
        //then we get the relative rotation of the wheel in a range of 360 degrees
        //and divide that by the segment length, that will be which segment we landed on
        //however, it could return results like 4.1. 4.1 visually is really on the 5th segment
        //so we take the ceiling of the result
        //Because we take the ceiling, its possible to get a result like (length + 0.9) which ceils to length + 1
        //That would cause an indexOutOfBounds, so we take the Math.min between the result and the total length
        int result = Math.min((int) Math.ceil(((segmentLength+(360-currentRotation))) / segmentLength), wheelRestaurantNames.length);
        String restaurant = wheelRestaurantNames[result-1];


        //The animation interpolator determines how the 'physics' of the spin look
        //previous we were using LinearInterpolator, however this one looks nicer
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        //how long the animation lasts in ms
        //also determines speed... longer animation, slower spin.
        int duration = 1500;
        anim.setDuration(duration);

        //needed for the animation to not reset after the spin
        anim.setFillEnabled(true);
        anim.setFillAfter(true);

        //start the animation
        refreshImage.startAnimation(anim);
        //grab the system time to make sure the thread waits enough
        long time = System.currentTimeMillis();
        //We want to display a popup after the animation plays
        //however, we need to wait for the animation to finish
        //and we need to start downloading the restaurant image as soon
        //as possible so the user doesn't have to wait
        //So, we make a thread.
        MainActivity activity = this;
        Executor executor = command -> new Thread(command).start();
        executor.execute(() -> {
            //grab the restaurant that the wheel chose
            Business business = null;
            for(Business b : restaurants) {
                if (b.getName().startsWith(restaurant)) {
                    business = b;
                }
            }
            //start downloading the image asap
            assert business != null;
            Drawable img = this.getImageFromUrl(business.getImageUrl());
            //the amount of time that has passed while downloading the image
            //in milliseconds
            long timeTaken = System.currentTimeMillis()-time;
            //the download was faster than the animation
            if(timeTaken < duration) {
                //then we need to wait for the animation
                //offset is the extra time we wait after the animation ends
                //so that the user can see their result for a moment.
                //zero makes the popup display instantly after the wheel stops
                int offset = 200;
                int waitTime = (int) ((duration - timeTaken) + offset);
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            //If the download was slower then the animation, we don't wait at all
            //display it ASAP!

            //Run the deployPopup code on the main thread, you can't do it on
            //non ui-threads
            activity.runOnUiThread(() -> deployPopup(view, restaurant, img));
        });


    }
}
