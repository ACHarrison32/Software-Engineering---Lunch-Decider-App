package us.four.lunchroulette;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO: Fix android permissions not allowing network by default
        //List of resturants that will be pulled from Yelp
        List<String> restaurants = new ArrayList<>();
        //Grab current context so the concurrent part can reference it
        Context context = this;
        //instance of Yelp Fusion api factory
        YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
        //Access GPS from the user.
        //TODO: Fallback method if this doesn't work, allow ZIP input
        GPSTracker tracker = new GPSTracker(this);
        if(tracker.canGetLocation) {
            tracker.getLocation();
            try {
                //This API key is limited in requests per day, but scales in load if we get more users and contact yelp
                //TODO: Maybe store this key in a file? Or even in a remote server so it can be changed and is harder to datamine
                YelpFusionApi yelpFusionApi = apiFactory.createAPI("WWc44gE8YXQor0rQC5cuPTmh1R6Bq6fhqMxJXDqxoRlefB-NjmNyOgVjggoq4E7NQ-g5grrk_rYewxMATnO_DkGIfrtfzohzxEL3FfoBZXLREfjnOG4JZGuMDlM0ZHYx");
               //HashMap for Yelp parameters
                Map<String, String> params = new HashMap<>();

                // general placeholder params, good for a default startup screen
                params.put("radius", "10000");
                params.put("open_now", "true");
                params.put("term", "food");
                params.put("sort_by", "rating");
                //params.put("latitude", tracker.getLatitude() + "");
                //params.put("longitude", tracker.getLongitude() + "");

                //For testing, use predefined coords. For production, get user GPS.
                params.put("latitude", "33.9788691");
                params.put("longitude", "-98.5391547");
                //call yelp api async (no networking on main thread allowed)

                Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
                Callback<SearchResponse> callback = new Callback<SearchResponse>() {
                    @Override
                    public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                        SearchResponse searchResponse = response.body();
                        for (Business b : searchResponse.getBusinesses()) {
                            //we only want to add about 6 resturants.
                            if(restaurants.size() < 6)
                                restaurants.add(b.getName());
                        }


                        //Get the wheel image view
                        ImageView wheelImage = findViewById(R.id.imageView2);

                        //string list to appear in the wheel
                        //can be up to like 10 long, only limited by how many colors you give it
                        //Any more than 6 and you risk having text spacing issues
                        String[] roast = restaurants.toArray(new String[0]);



                        //create an instance of the wheel object
                        Wheel wheel = new Wheel(context, roast);
                        //set our image displayed to the image made by the wheel class
                        wheelImage.setImageDrawable(wheel.getImage());
                        //we only want to show the wheel image after its been created
                        wheelImage.setVisibility(View.VISIBLE);
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Can't enable wheel image until after content view is set
        ImageView wheelImage = findViewById(R.id.imageView2);
        wheelImage.setVisibility(View.INVISIBLE);


    }
    public void filtersButton_Click(View view) {
        //open Filters activity
        Intent intent = new Intent("filters.intent.action.Launch");
        startActivity(intent);

    }

    //seperate rotation int to be stored
    //helps with animation smoothness, wheel spins again from its previous rotation
    private int currentRotation = 0;
    @SuppressLint("UseCompatLoadingForDrawables")
    public void spin(View view) {


        //Commented out code was for showing the result of the spin
        //WILL BE USED LATER

        //IMPORTANT!! WE DON'T CHOOSE A RANDOM RESTURANT FROM THE SPIN
        //INSTEAD WE CHOOSE A RANDOM ROTATION
        //FROM THERE WE USE MATH TO FIND THE RESTURANT
        //FOR EXAMPLE: WHICH RESTURANT IS 790 DEGREES OF ROTATION?

        //String[] roast = wheel.getEntries();

        //TextView textview2 = (TextView) this.findViewById(R.id.textView2);
        //textview2.setVisibility(View.VISIBLE);

        //determines how much the wheel should spin
        //since the animation only takes 1000ms, more angle means a faster spin
        int rotateAmount = ((int) (Math.random() * 360) + 720);
        //grab the imageview of our wheel
        ImageView refreshImage = this.findViewById(R.id.imageView2);
        //create a RotateAnimation
        RotateAnimation anim = new RotateAnimation(currentRotation, currentRotation + rotateAmount,Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        //save the spot of our current spin, this way its consistent if the user respins
        currentRotation = (currentRotation + rotateAmount) % 360;

        //used for getting the result
        //crazy math :)
        //int segmentLength = (360/roast.length);
        //int result = Math.min((int) Math.ceil(((segmentLength+(360-currentRotation))) / segmentLength), 8);
        //textview2.setText(roast[result-1]);

        //The animation interpolator determines how the 'physics' of the spin look
        //previous we were using LinearInterpolator, however this one looks nicer
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        //how long the animation lasts in ms
        //also determines speed... longer animation, slower spin.
        anim.setDuration(1000);

        //needed for the animation to not reset after the spin
        anim.setFillEnabled(true);
        anim.setFillAfter(true);

        //Go!!
        refreshImage.startAnimation(anim);
    }
}
