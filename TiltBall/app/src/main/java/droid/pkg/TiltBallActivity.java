// Example of using sensor data to move ball from:
// http://www.codeproject.com/Articles/228656/Tilt-Ball-Walkthrough
// by Mike Waddell
// minor changes by Mike Scott

package droid.pkg;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;


public class TiltBallActivity extends Activity {
	
	BallView mBallView = null;
	Handler RedrawHandler = new Handler(); //so redraw occurs in main thread
	Timer mTmr = null;
	TimerTask mTsk = null;
	int mScrWidth, mScrHeight;
    android.graphics.PointF mBallPos, mBallVelocity;
    float mPrevXAcc, mPrevYAcc;
    long mPrevTime;
    
    final float ACC_FUDGE_FACTOR = .5f;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //hide title bar
        getWindow().setFlags(0xFFFFFFFF,
        		LayoutParams.FLAG_FULLSCREEN|LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //create pointer to main screen
        final FrameLayout mainView = (FrameLayout) findViewById(R.id.main_view);

        //get screen dimensions
        Display display = getWindowManager().getDefaultDisplay();  
        mScrWidth = display.getWidth(); 
        mScrHeight = display.getHeight();
    	mBallPos = new android.graphics.PointF();
    	mBallVelocity = new android.graphics.PointF();
        
        //create variables for ball position and speed
        mBallPos.x = mScrWidth / 2; 
        mBallPos.y = mScrHeight / 2; 
        mBallVelocity.x = 0;
        mBallVelocity.y = 0; 
        mPrevTime = System.currentTimeMillis();
        mPrevXAcc = 0;
        mPrevYAcc = 0;
        
        //create initial ball
        mBallView = new BallView(this, mBallPos.x, mBallPos.y, 20);
                
        mainView.addView(mBallView); //add ball to main screen
        mBallView.invalidate(); //call onDraw in BallView
        		
        //listener for accelerometer, use anonymous inner class
        
//        SensorManagerSimulator sm = SensorManagerSimulator.getSystemService(this, SENSOR_SERVICE);
//        sm.connectSimulator();
        
        SensorManager sm = ((SensorManager)getSystemService(Context.SENSOR_SERVICE));
        
        sm.registerListener(new SensorEventListener() {
    			@Override  
    			public void onSensorChanged(SensorEvent event) {  
    			    //set ball speed based on phone tilt (ignore Z axis) 
    				// speed set equal to acceleration
//    				mBallVelocity.x = -event.values[0];
//    				mBallVelocity.y = event.values[1];
    				
    				// original values too sensitive, tweak based on average of
    				// new acceleration value and previous acceleration value
    				float xA = -event.values[0];
    				float yA = event.values[1];
    				float aveXA = (xA + mPrevXAcc) / 2;
    				float aveYA = (yA + mPrevYAcc) / 2;
    				long currentTime = System.currentTimeMillis();
    				long elapsedTime = currentTime - mPrevTime;
                    // HACK. We are setting velocity equal to average acceleration
    				mBallVelocity.x += aveXA * elapsedTime / 1000 / ACC_FUDGE_FACTOR; // acceleration in m/sec^2
    				mBallVelocity.y += aveYA * elapsedTime / 1000 / ACC_FUDGE_FACTOR;
    				
    				mPrevXAcc = xA;
    				mPrevYAcc = yA;
    				mPrevTime = currentTime;
    				
    				//timer event will redraw ball
    			}
        		@Override  
        		public void onAccuracyChanged(Sensor sensor, int accuracy) {} //ignore this event
        	},
        	
        	sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
        
//        	((SensorManager)getSystemService(Context.SENSOR_SERVICE))
//        	.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_NORMAL);
        		
        //listener for touch event 
        mainView.setOnTouchListener(new android.view.View.OnTouchListener() {
	        public boolean onTouch(android.view.View v, android.view.MotionEvent e) {
	        	//set ball position based on screen touch
	        	mBallPos.x = e.getX();
	        	mBallPos.y = e.getY();
	        	mBallVelocity.x = 0;
	        	mBallVelocity.y = 0;
	        	mPrevXAcc = 0;
	        	mPrevYAcc = 0;
	        	mPrevTime = System.currentTimeMillis();
    			//timer event will redraw ball
	        	return true;
	        }}); 
    } //OnCreate
    
    //listener for menu button on phone
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Exit"); //only one menu item
        return super.onCreateOptionsMenu(menu);
    }
    
    //listener for menu item clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// Handle item selection    
    	if (item.getTitle() == "Exit") //user clicked Exit
    		finish(); //will call onPause
   		return super.onOptionsItemSelected(item);    
    }
    
    //For state flow see http://developer.android.com/reference/android/app/Activity.html
    @Override
    public void onPause() //app moved to background, stop background threads
    {
    	mTmr.cancel(); //kill\release timer (our only background thread)
    	mTmr = null;
    	mTsk = null;
    	super.onPause();
    }
    
    @Override
    public void onResume() //app moved to foreground (also occurs at app startup)
    {
        //create timer to move ball to new position
        mTmr = new Timer(); 
        mTsk = new TimerTask() {
			public void run() {
				//if debugging with external device, 
				//  a cat log viewer will be needed on the device
				android.util.Log.d(
				    "TiltBall","Timer Hit. x:" + mBallPos.x + " y:" + mBallPos.y);
				
			    //move ball based on current velocity
				mBallPos.x += mBallVelocity.x;
				mBallPos.y += mBallVelocity.y;
				
				// Log.d("TiltBall", "Velocities: " + mBallVelocity.x + " " + mBallVelocity.y);
				// Log.d("TiltBall", "Accelerations: " + mPrevXAcc + " " + mPrevYAcc);
				// if ball hits edge, bounce a little
				// old options: 
				// 1. wrap to other side
				// 2. stop and zero acceleration

				if (mBallPos.x + mBallView.mR  >= mScrWidth) {
					mBallPos.x = mScrWidth - mBallView.mR;
					mBallVelocity.x = -(mBallVelocity.x * .5f);
				}
				if (mBallPos.y + mBallView.mR >= mScrHeight) {
					mBallVelocity.y = -(mBallVelocity.y * .5f);
					mBallPos.y = mScrHeight - mBallView.mR;
				}
				if (mBallPos.x - mBallView.mR <= 0)  {
					mBallVelocity.x = -(mBallVelocity.x * .5f);
					mBallPos.x = mBallView.mR;
				}
				if (mBallPos.y - mBallView.mR <= 0) {
					mBallVelocity.y = -(mBallVelocity.y * .5f);
					mBallPos.y = mBallView.mR;
				}
				
				//update ball class instance
				mBallView.mX = mBallPos.x;
				mBallView.mY = mBallPos.y;
				mBallView.vX = mBallVelocity.x;
				mBallView.vY = mBallVelocity.y;
				
				//redraw ball. Must run in background thread to prevent thread lock.
				RedrawHandler.post(new Runnable() {
				    public void run() {	
					   mBallView.invalidate();
				  }});
			}}; // TimerTask

        mTmr.schedule(mTsk, 10, 10); //start timer
        super.onResume();
    } // onResume
    
    @Override
    public void onDestroy() //main thread stopped
    {
    	super.onDestroy();
    	System.runFinalizersOnExit(true); //wait for threads to exit before clearing app
    	// WHO WROTE THIS???? android.os.Process.killProcess(android.os.Process.myPid());  //GACKYYYYYY remove app from memory 
    }
    
    //listener for config change. 
    //This is called when user tilts phone enough to trigger landscape view
    //we want our app to stay in portrait view, so bypass event 
    @Override 
    public void onConfigurationChanged(Configuration newConfig)
	{
       super.onConfigurationChanged(newConfig);
	}

}