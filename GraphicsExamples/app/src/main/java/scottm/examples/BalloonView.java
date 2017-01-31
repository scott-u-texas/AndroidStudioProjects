package scottm.examples;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by scottm on 3/9/2016.
 */
public class BalloonView extends View {

    private static final String TAG = "BalloonView";


    private static final int MAX_BALLOONS = 20;
    private static final
        double newBalloonsPerFrame = 1.0 / 25;

    private Paint paint;

    private HashSet<Balloon> balloons;
    private Random random;


    public BalloonView(Context context) {
        super(context);
        initialize();
    }

    public BalloonView (Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();

    }

    public BalloonView(Context context, AttributeSet attrs,
                       int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }


    private void initialize() {
        balloons = new HashSet<Balloon>();
        random = new Random();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        updateBalloons();
        double randomValue = random.nextDouble();
        if (balloons.size() == 0
                || randomValue < newBalloonsPerFrame) {
            addBalloon(height, width);
        }
        for (Balloon b : balloons) {
            b.draw(canvas, paint);
        }
    }

    private void updateBalloons() {
        Iterator<Balloon> it = balloons.iterator();
        while (it.hasNext()) {
            Balloon b = it.next();
            b.update();
            if (b.offView()) {
                it.remove();
            }
        }
    }

    private void addBalloon(int height, int width) {
        int x = random.nextInt(width);
        int radius = random.nextInt(25) + 10;
        int speed = random.nextInt(5) + 5;
        Balloon b = new Balloon(x, height + radius + 1, radius, speed);
        balloons.add(b);
    }


}
