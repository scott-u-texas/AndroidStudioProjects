package scottm.examples;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by scottm on 3/9/2016.
 */
public class Balloon {

    private static Random ourRandom = new Random(249);

    private static final int MAX_COLOR_INTENSITY = 256;

    private int x;
    private int y;
    private int radius;
    private int speed; // pixels per frame
    private int color;

    public Balloon(int x, int y, int radius, int speed) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.speed = speed;
        initColor();
    }

    private void initColor() {
        int r = ourRandom.nextInt(MAX_COLOR_INTENSITY);
        int g = ourRandom.nextInt(MAX_COLOR_INTENSITY);
        int b = ourRandom.nextInt(MAX_COLOR_INTENSITY);
        color =  Color.rgb(r, g, b);
    }

    public void update() {
        y -= speed;
    }

    public void draw(Canvas c, Paint p) {
        p.setColor(color);
        c.drawCircle(x, y, radius, p);
    }

    public boolean offView() {
        return y + radius < 0;
    }
}
