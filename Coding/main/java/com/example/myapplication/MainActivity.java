package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;


import java.util.Random;


public class MainActivity extends AppCompatActivity {

    public static Bitmap rotateBitmap180(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    static int[] colors = {0XFFFF5733, 0XFFFFE633, 0XFF33FFA8, 0XFF33E5FF, 0XFFE533FF, 0XFFFF36D6, 0XFFDD3333, 0XFF33FF33};

    public static Drawable createPieChartDrawable(Context context, String[] strings) {
        int width = 500;
        int height = 500;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        RectF rectF = new RectF(0, 0, width, height);
        float anglePerPartition = 360f / strings.length;
        Random random = new Random();

        for (int i = 0; i < strings.length; i++) {
            //paint.setColor(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            paint.setColor(colors[i % 10]);
            canvas.drawArc(rectF, i * anglePerPartition, anglePerPartition, true, paint);

            Path path = new Path();
            path.addArc(rectF, i * anglePerPartition, anglePerPartition);
            paint.setColor(Color.BLACK);
            paint.setTextSize(30);
            paint.setLetterSpacing(0.5f);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawTextOnPath(strings[i], path, 0, height / 4f, paint);
        }
        Bitmap bitmap2 = rotateBitmap180(bitmap);
        BitmapDrawable werg = new BitmapDrawable(context.getResources(), bitmap2);

        return werg;
    }

    public Bitmap bytesToBitmap(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("The byte array can't be null or empty!");
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView werg = this.findViewById(R.id.imageView2);

        String[] roast = {"1", "2", "3", "4", "5", "6", "7", "8"};
        Drawable chart =  createPieChartDrawable(this, roast);
        werg.setImageDrawable(chart);





//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ezanas();
//            }
//        });
    }
    public void filtersButton_Click(View view) {
        Snackbar.make(findViewById(android.R.id.content), "Feature not yet implemented",
                        Snackbar.LENGTH_SHORT)
                .show();

    }
    int currentRotation = 0;
    @SuppressLint("UseCompatLoadingForDrawables")
    public void spin(View view) {
        String[] roast = {"1", "2", "3", "4", "5", "6", "7", "8"};

        //TextView textview2 = (TextView) this.findViewById(R.id.textView2);
        //textview2.setVisibility(View.VISIBLE);
        int rotateAmount = ((int) (Math.random() * 360) + 420);
        ImageView refreshImage = (ImageView) this.findViewById(R.id.imageView2);
        //refreshImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.placeholder_wheel, null));
        RotateAnimation anim = new RotateAnimation(currentRotation, currentRotation + rotateAmount,Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        currentRotation = (currentRotation + rotateAmount) % 360;
        int segmentLength = (360/roast.length);
        int result = Math.min((int) Math.ceil(((segmentLength+(360-currentRotation))) / segmentLength), 8);
        //textview2.setText(roast[result-1]);
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(500);
        anim.setFillEnabled(true);
        anim.setFillAfter(true);
        refreshImage.startAnimation(anim);
//    startSpin();
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                stopSpin();
//            }
//        }, 2000);
    }
}