package scottm.examples;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GraphicsView extends View {

    private static final String TAG = "GraphicsView";

    private int y;
    private int x;

    private final int CIRCLE_RADIUS = 100;
    private static final int STOPPED = 0;
    private static final int RUNNING = 1;
    private static final int SPEED = 5; // pixels per frame

    private int moveDelay = 10;

    private Paint p;


    // for checking frame rate

    private long prevTime = System.currentTimeMillis();
    private long startTime = System.currentTimeMillis();
    private int frameCount;
    private int mode;

    public GraphicsView(Context context) {
        super(context);
        initialize();
    }

    public GraphicsView (Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();

    }

    public GraphicsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }


    private void initialize() {
        this.setBackgroundColor(Color.WHITE);
        y = -CIRCLE_RADIUS;
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        // p = new Paint();
        p.setColor(getResources().getColor(R.color.BurntOrange));
        mode = STOPPED;
        update();
    }

    private void handleFrameRateChecks() {
        long currTime = System.currentTimeMillis();
        // long diff = currTime - prevTime;
        // prevTime = currTime;
        // Log.d(TAG, "time diff: " + diff);

        if(frameCount < 30) {
            frameCount++;
        }
        else {
            frameCount = 0;
            long timeDiff = currTime - startTime;
            startTime = currTime;
            double frameRate = 1000.0 / (timeDiff / 30.0) ;
            Log.d(TAG, "frame rate: " + (int) frameRate);
            Log.d(TAG, "timediff: " + timeDiff);
        }
    }

    private RefreshHandler mRedrawHandler = new RefreshHandler(this);


    private static class RefreshHandler extends Handler {

        private GraphicsView g;

        private RefreshHandler(GraphicsView g) {
            this.g = g;
        }

        @Override
        public void handleMessage(Message msg) {
            g.update();
            g.invalidate();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };


    public void update() {

        if (mode == RUNNING) {
            handleFrameRateChecks();
            long now = System.currentTimeMillis();
            if (now - prevTime > moveDelay) {
                prevTime = now;
                x = getWidth() / 2;
                y += SPEED;
                if(y > getHeight())
                    mode = STOPPED;
            }
            mRedrawHandler.sleep(moveDelay);
        }
    }

    public void toggleAnimation() {
        if(mode == RUNNING) {
            mRedrawHandler.removeMessages(0);
            mode = STOPPED;
        }
        else if(mode == STOPPED && y < getHeight()) {
            mode = RUNNING;
            update();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // badAnimation(canvas);

        //		if(mode == STOPPED) {
        //		    x = canvas.getWidth() / 2;
        //		    y = canvas.getHeight() / 2;
        //		}

        canvas.drawCircle(x, y, CIRCLE_RADIUS, p);

    }

    private void badAnimation(Canvas canvas) {
        handleFrameRateChecks();

        int x = getWidth() / 2;

        canvas.drawCircle(x, y, CIRCLE_RADIUS, p);
        y += SPEED;
        if(y < getHeight())
            invalidate();

    }
}

//canvas.drawColor(Color.WHITE);
//
//int w = getWidth();
//int h = getHeight();
//
//// show sample gradients
//// linear gradient
//Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
//LinearGradient lg = new LinearGradient(0, 0, 25, 50, 
//      Color.RED, Color.BLUE, Shader.TileMode.MIRROR);
//p.setShader(lg);
//canvas.drawOval(new RectF(0, 0, 300, 200), p);
//
//// radial gradient
//RadialGradient rg = new RadialGradient(200, 400, 125, 
//      Color.BLUE, Color.GREEN, Shader.TileMode.MIRROR);
//p.setShader(rg);
//canvas.drawCircle(200, 325, 125, p);

//// sweep gradient
//int numColors = 4;
//int angleIncrement = 360 / numColors;
//int[] rainbow = new int[numColors * 2];
//float[] hsv = {0, 1, 1};
//for(int i = 0; i < rainbow.length / 2; i++) {
//  rainbow[i] = Color.HSVToColor(hsv);
//  hsv[0] += angleIncrement;
//}
//for(int i = rainbow.length / 2; i < rainbow.length; i++) {
//  rainbow[i] = rainbow[rainbow.length - i];
//}
//SweepGradient sg = new SweepGradient(300, 600, rainbow, null);
//p.setShader(sg);
//canvas.drawCircle(300, 600, 125, p);

//SweepGradient sg = new SweepGradient(300, 600, 
//      new int[] {Color.RED, Color.YELLOW, Color.RED}, 
//      null);
//p.setShader(sg);
//canvas.drawCircle(300, 600, 125, p);

