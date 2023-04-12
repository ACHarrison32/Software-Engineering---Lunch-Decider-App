package us.four.lunchroulette;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.google.android.material.snackbar.Snackbar;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Generate a response for the wheel
        List<String> resturants = new ArrayList<>();
        Context context = this;
        YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
        GPSTracker tracker = new GPSTracker(this);
        if(tracker.isGPSEnabled) {
            tracker.getLocation();
            try {
                YelpFusionApi yelpFusionApi = apiFactory.createAPI("WWc44gE8YXQor0rQC5cuPTmh1R6Bq6fhqMxJXDqxoRlefB-NjmNyOgVjggoq4E7NQ-g5grrk_rYewxMATnO_DkGIfrtfzohzxEL3FfoBZXLREfjnOG4JZGuMDlM0ZHYx");
                Map<String, String> params = new HashMap<>();

// general params
                params.put("radius", "10000");
                params.put("open_now", "true");
                params.put("term", "food");
                params.put("term", "lunch");
                params.put("sort_by", "rating");
                //params.put("latitude", tracker.getLatitude() + "");
                //params.put("longitude", tracker.getLongitude() + "");
                params.put("latitude", "33.9788691");
                params.put("longitude", "-98.5391547");
                Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
                Callback<SearchResponse> callback = new Callback<SearchResponse>() {
                    @Override
                    public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                        SearchResponse searchResponse = response.body();
                        //System.out.println(searchResponse.getBusinesses().);
                        for (Business b : searchResponse.getBusinesses()) {
                            if(resturants.size() < 6)
                                resturants.add(b.getName());
                        }
                        // Update UI text with the searchResponse.
                       // workDone = true;


                        //Get the wheel image view
                        ImageView wheelImage = findViewById(R.id.imageView2);

                        //string list to appear in the wheel
                        //can be up to like 10 long, only limited by how many colors you give it
                        //Any more than 6 and you risk having text spaceing issues
                        String[] roast = resturants.toArray(new String[0]);



                        //create an instance of the wheel object
                        Wheel wheel = new Wheel(context, roast);
                        //set our image displayed to the image made by the wheel class
                        wheelImage.setImageDrawable(wheel.getImage());
                        wheelImage.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<SearchResponse> call, Throwable t) {
                        // HTTP error happened, do something to handle it.
                    }
                };
                call.enqueue(callback);
//               while(lock.size() == 0) {
//                   Thread.sleep(10);
//               }
//                while(!workDone) {
//                    Thread.sleep(5);
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //auto generated
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView wheelImage = findViewById(R.id.imageView2);
        wheelImage.setVisibility(View.INVISIBLE);


    }
    public void filtersButton_Click(View view) {
        Intent intent = new Intent("filters.intent.action.Launch");
        startActivity(intent);
//        Snackbar.make(findViewById(android.R.id.content), "Feature not yet implemented",
//                        Snackbar.LENGTH_SHORT)
//                .show();

    }

    int currentRotation = 0;
    @SuppressLint("UseCompatLoadingForDrawables")
    public void spin(View view) {


        //Commented out code was for showing the result of the spin
        //will likely be used later

        //String[] roast = wheel.getEntries();

        //TextView textview2 = (TextView) this.findViewById(R.id.textView2);
        //textview2.setVisibility(View.VISIBLE);

        //determines how much the wheel should spin
        //since the animation only takes 500ms, more angle means a faster spin
        int rotateAmount = ((int) (Math.random() * 360) + 720);
        //grab the imageview of our wheel
        ImageView refreshImage = this.findViewById(R.id.imageView2);
        //create a RotateAnimation
        RotateAnimation anim = new RotateAnimation(currentRotation, currentRotation + rotateAmount,Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        //save the spot of our current spin, this way its consistent if the user respins
        currentRotation = (currentRotation + rotateAmount) % 360;

        //used for getting the result
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
