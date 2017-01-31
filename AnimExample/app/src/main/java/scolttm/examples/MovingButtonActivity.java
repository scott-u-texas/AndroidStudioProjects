package scolttm.examples;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Random;

public class MovingButtonActivity extends Activity {
    
    private Random randNumGen;
    private static final int NUM_SHADES = 256;

    public static int TWEEN = 0;
    public static int PROPERTY = 1;
    
    public static final String ANIMATION_TAG = "anim";
    private static String TAG = "Button Animation";

    private int animationType;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_move);
        randNumGen = new Random();
        Intent intent = getIntent();
        animationType = TWEEN;
        if(intent.hasExtra(ANIMATION_TAG)) {
            animationType = intent.getExtras().getInt(ANIMATION_TAG);
        }
        setUpLayoutListener();
    }

    private void setUpLayoutListener() {
        final LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout_button);

        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int width = layout.getMeasuredWidth();
                int height = layout.getMeasuredHeight();
                Log.d(TAG, "layout width: " + width + ", layout height: " + height);
                initAnimation(height);
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initAnimation(int height) {
        if(animationType == TWEEN)
            tweenedAnimation();
        else
            propertyAnimation(height);
    }

    private void propertyAnimation(int height) {
        Button movingButton 
            = (Button) findViewById(R.id.change_background);
        ObjectAnimator anim 
            = ObjectAnimator.ofFloat(movingButton, "y", 0, height);

        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.setDuration(6000);
        anim.start();
    }

    public void tweenedAnimation() {
        Button movingButton 
                = (Button) findViewById(R.id.change_background);
        movingButton.startAnimation(
                AnimationUtils.loadAnimation(this, 
                        R.anim.up_and_down));
    }
    
    public void changeBackground(View v) {
         // example of using ViewAnimator
//        if(v.getAlpha() == 0)
//            v.animate().setDuration(3000).alpha(1);
//        else
//            v.animate().alpha(0);
        View target = findViewById(R.id.linear_layout_button);
        int red = randNumGen.nextInt(NUM_SHADES);
        int green = randNumGen.nextInt(NUM_SHADES);
        int blue = randNumGen.nextInt(NUM_SHADES);
        target.setBackgroundColor(Color.argb(255, red, green, blue));
    }
}
