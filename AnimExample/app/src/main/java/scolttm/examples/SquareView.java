package scolttm.examples;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SquareView extends View {
    
    private static final String TAG = "SquareView";
    
    private Paint redPaint;
    private Paint bluePaint;
    
    private Rect mRect;
    
    
    // for checking frame rate
    public SquareView (Context context, AttributeSet attrs) {
        super(context);
        
        this.setBackgroundColor(Color.WHITE);
        redPaint = new Paint(Color.RED);
        bluePaint = new Paint(Color.BLUE);
        
        mRect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float top = canvas.getWidth() / 2 - mRect.width / 2;
        float left = canvas.getHeight() / 2 - mRect.height / 2;
        RectF rect = new RectF(left, top, left + mRect.width, top + mRect.height);
        
    }
    
    private static class Rect {
        private int width;
        private int height;
        private int x;
        private int y;
        private float rotY;
        
        public Rect () {
            width = 250;
            height = 100;
            x = y = 0;
        }
        
        public void setRotationY(float rot) {
            rotY = rot;
        }
        
        public float getRotationY() {
            return rotY;
        }
    }
}
