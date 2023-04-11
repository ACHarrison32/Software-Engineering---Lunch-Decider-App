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


import java.util.Random;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //auto generated
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the wheel image view
        ImageView wheelImage = this.findViewById(R.id.imageView2);

        //string list to appear in the wheel
        //can be up to like 10 long, only limited by how many colors you give it
        //Any more than 6 and you risk having text spaceing issues
        String[] roast = {"Red Robin", "Chipotle", "McDonalds", "Qdoba", "Taco Bell", "Braums", "Pizza Hut", "Wendy's"};

        //create an instance of the wheel object
        Wheel wheel = new Wheel(this, roast);
        //set our image displayed to the image made by the wheel class
        wheelImage.setImageDrawable(wheel.getImage());

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