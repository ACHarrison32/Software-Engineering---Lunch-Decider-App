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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wheel {

	private final int[] defaultColors = new int[] {0xFFFF7F50, 0xFFFFA500, 0xFFFFC0CB, 0xFFFF1493, 0xFFFF00FF, 0xFF00FFFF, 0xFF00FF7F, 0xFFFFFF00, 0xFFFFD700};

    private String[] entries;
    private int[] colors = defaultColors;
    private Drawable image = null;

    public Wheel(Context context, String[] entries) {
        this.entries = entries;
        this.colors = defaultColors;
        this.image = this.createPieChartDrawable(context, entries);
    }

    public Wheel(Context context, String[] entries, int[] colors) {
        this.entries = entries;
        this.colors = colors;
        this.image = this.createPieChartDrawable(context, entries);
    }


    public Drawable createPieChartDrawable(Context context, String[] strings) {

        int width = 700;
        int height = 700;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        RectF rectF = new RectF(0, 0, width, height);
        float anglePerPartition = 360f / strings.length;

        for (int i = 0; i < strings.length; i++) {
            paint.setColor(colors[i % 10]);
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
                    System.out.println(s);
                    if((temp.get(index) + s).length() < (15 - (lines*1.5))) {
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
                System.out.println(temp.size() + " " + split.length);
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
        BitmapDrawable werg = new BitmapDrawable(context.getResources(), bitmap2);

        return werg;
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
