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
            if(i == strings.length-1)
                paint.setColor(colors[colors.length-1]);
            canvas.drawArc(rectF, i * anglePerPartition, anglePerPartition, true, paint);

            Path path = new Path();
            path.addArc(rectF, i * anglePerPartition, anglePerPartition);
            paint.setColor(Color.BLACK);
            paint.setTextSize(30);
            paint.setLetterSpacing(0.18f);
            paint.setTypeface(Typeface.SANS_SERIF);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawTextOnPath(strings[i], path, 0, height / 8f, paint);
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
