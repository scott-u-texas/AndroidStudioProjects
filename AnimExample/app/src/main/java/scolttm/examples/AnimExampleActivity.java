package scolttm.examples;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

public class AnimExampleActivity extends Activity {
	
	private boolean spin;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        spin = false;
    }
    
    public void animate(View v) {
    	View target = (View) findViewById(R.id.linearLayout1);
    	if(spin)
    		target.startAnimation(AnimationUtils.loadAnimation(this, R.anim.spin_rotate));
    	else
    		target.startAnimation(AnimationUtils.loadAnimation(this, R.anim.hyperspace));
    	spin = !spin;
    }
    
    public void startTween(View v) {
        startMovingButtonActivity(MovingButtonActivity.TWEEN);
    }
    
    public void startProperty(View v) {
        startMovingButtonActivity(MovingButtonActivity.PROPERTY);
    }
    
    private void startMovingButtonActivity(int type) {
        Intent startIntent = new Intent(this, MovingButtonActivity.class);
        startIntent.putExtra(MovingButtonActivity.ANIMATION_TAG, type);
        startActivity(startIntent);
    }
    
}