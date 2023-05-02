package us.four.lunchroulette;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class Wheel {

    //Default list of colors that will make up each segment of the wheel
	private final int[] defaultColors = new int[] {0xFFFF7F50, 0xFFFFA500, 0xFFFFC0CB, 0xFFFF1493, 0xFFFF00FF, 0xFF00FFFF, 0xFF00FF7F, 0xFFFFFF00, 0xFFFFD700};
    //colors int, may be overridden from defaultColors by another constructor
    private int[] colors = defaultColors;
    //List of string entries, for the purposes of this app's implementation that means resturants.
    private final String[] entries;
    //The Android Drawable object (image) that this function returns.
    private Drawable image;

    //default constructor, contains the app graphics context and a string list of entries
    public Wheel(Context context, String[] entries) {
        this.entries = entries;
        this.colors = defaultColors;
        this.image = this.createPieChartDrawable(context, entries);
    }

    //Overloaded constructor to specify wheel color.
    public Wheel(Context context, String[] entries, int[] colors) {
        this.entries = entries;
        this.colors = colors;
        this.image = this.createPieChartDrawable(context, entries);
    }

    //Input: Graphics Context, list of strings.
    //Returns: Drawable of a pie chart
    private Drawable createPieChartDrawable(Context context, String[] strings) {
        if(strings.length == 0) {
            return AppCompatResources.getDrawable(context, R.drawable.placeholder_wheel);
        }
        //sets resolution
        //should be square for this application
        //keep in mind, android will rescale to whatever is set in the activity xml
        int width = 700;
        int height = 700;

        //Create image bitmap and canvas
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        //we're gonna paint on it
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        //rectangle spanning the image
        RectF rectF = new RectF(0, 0, width, height);
        //the number of degrees of one segment of the pie chart. Depends on string length.
        float anglePerPartition = 360f / strings.length;

        for (int i = 0; i < strings.length; i++) {
            //if we have enough defined colors
            if(i < colors.length) {
                //set the segment color depending on which string this is
                paint.setColor(colors[i % colors.length]);
            } else {
                //if we run out of colors, default to black
                paint.setColor(0XFF000000);
            }
            if(i == strings.length-1)
                paint.setColor(colors[colors.length-1]);
            canvas.drawArc(rectF, i * anglePerPartition, anglePerPartition, true, paint);

            Path path = new Path();
            path.addArc(rectF, i * anglePerPartition, anglePerPartition);
            paint.setColor(Color.BLACK);
            paint.setTextSize(28);
            paint.setLetterSpacing(0.15f);
            paint.setTypeface(Typeface.SANS_SERIF);
            paint.setTextAlign(Paint.Align.CENTER);
            //canvas.drawText(strings[i], 0, height / 9f, paint);
            if(strings[i].length() > 17) {
                String[] split = strings[i].split(" ");
                List<String> temp = new ArrayList<>();
                int index = 0;
                temp.add("");
                int lines = 0;
                for(String s : split) {
                    if((temp.get(index) + s).length() < (17 - (lines*2))) {
                        if(lines < 4)
                            temp.set(index, temp.get(index) + " " + s);
                    } else {
                        temp.add(s);
                        index++;
                    }
                    lines++;
                }
//                for(int l = 0; l < split.length; l++) {
//
//                    l++;
//                }
                split = temp.toArray(new String[0]);
                int yoff = 0;
                float hoff = 10f;
                for(String s : split) {
                   if(yoff == 0)
                       hoff = 10;
                   if(yoff == 1)
                       hoff = 7;
                   if(yoff == 2)
                       hoff = 5.4f;
                   if(yoff == 3)
                       hoff = 4.4f;
                    if(yoff == 4)
                        hoff = 3.7f;
                    paint.setLetterSpacing((0.15f + (0.12f * yoff)));
                    canvas.drawTextOnPath(s, path, 0, height / hoff, paint);
                    yoff++;
                }
            } else
                canvas.drawTextOnPath(strings[i], path, 0, height / 10f, paint);
        }
        Bitmap bitmap2 = this.rotateBitmap180(bitmap);

        return new BitmapDrawable(context.getResources(), bitmap2);
    }

    public Bitmap rotateBitmap180(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public Drawable getImage() {
        return this.image;
    }
    public String[] getEntries() {
        return this.entries;
    }
}
