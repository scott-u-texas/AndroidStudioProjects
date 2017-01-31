package droid.pkg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class BallView extends View {

    private static final int VELOCITY_LINE_WIDTH = 15;

	public float mX;
    public float mY;
    public  final int mR;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public float vX;
    public float vY;
    
    //construct new ball object
    public BallView(Context context, float x, float y, int r) {
        super(context);
        this.mX = x;
        this.mY = y;
        this.mR = r; //radius
    }
    	
    //called by invalidate()	
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStrokeWidth(1);
        //color hex is [transparency][red][green][blue]
        mPaint.setColor(0xFF00FF00); // green
        canvas.drawCircle(mX, mY, mR, mPaint);

        // draw the velocity indicators
        mPaint.setStrokeWidth(3);
        mPaint.setColor(0xFFFFFF00); // yellow
        canvas.drawLine(mX, mY, 
        		mX + vX * VELOCITY_LINE_WIDTH, mY, mPaint);
        mPaint.setColor(0xFF0000FF); // blue
        canvas.drawLine(mX, mY, 
        		mX, mY + vY * VELOCITY_LINE_WIDTH, mPaint);
        // Log.d("TiltBall", mX + vX * 100 + " " + mY + vY * 100);
    } 
}
