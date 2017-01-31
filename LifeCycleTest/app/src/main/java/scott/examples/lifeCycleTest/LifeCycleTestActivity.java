package scott.examples.lifeCycleTest;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LifeCycleTestActivity extends Activity {

	private static final String TAG = "LIFECYCLE TEST: ";
	private static final String NUM_TAG = "NUM_CALLS";
	public static final int GET_NAME = 131;

	private int onSaveInstanceState_NumCalls;
	private int onResumeCalls;

	private String showState = "";

	private MediaPlayer mediaPlayer;
	private boolean playsWell = true;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// activity being created
		super.onCreate(savedInstanceState);

		Log.d(TAG, "in onCreate Method");
		if(savedInstanceState != null) {
			showState = savedInstanceState.getString(TAG);
			onSaveInstanceState_NumCalls = savedInstanceState.getInt(NUM_TAG);
		}
		else {
			showState += " Not Created from a saved instance. ";
			onSaveInstanceState_NumCalls = 0;
		}
		Log.d(TAG, "" + onSaveInstanceState_NumCalls);
		setContentView(R.layout.main);

		TextView t = (TextView)findViewById(R.id.text);
		t.setText(showState);

		//		TextView tv = new TextView(this);
		//		tv.setGravity(Gravity.CENTER);
		//		// tv.setTextSize();
		//		tv.setText(showState);
		//		setContentView(tv);
		Log.d(TAG, "In onCreate STILL. Bundle parameter: " + savedInstanceState);
	}

    private void changeButtonPadding() {
        Button b = (Button) findViewById(R.id.clickForActivityButton);
        b.setPadding(40, 40, 30, 10);
    }

	private void handleOnResumeCalls() {
		onResumeCalls++;
		EditText et = (EditText)findViewById(R.id.onResumeCalls);
		et.setText(onResumeCalls + "");
	}

	protected void onStart() {
		// activity about to become visible
		super.onStart();
		Log.d(TAG, "in onStart Method");
	}

	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "in onRestart Method");
	}

	protected void onResume() {
		// activity has become visible and is now resumed
		super.onResume();
		// showState += "R+"; // add to String, String gets longer with each onResume call
		handleOnResumeCalls();
		Log.d(TAG, "in onResume Method");    	
	}

	protected void onPause() {
		// another activity is taking the focus and this activity is about to be paused
		super.onPause();
        handleMediaPlayer();
		Log.d(TAG, "in onPause Method");
	}

	protected void onStop() {
		// the activity is no longer visible. It is now stopped
		super.onStop();
		Log.d(TAG, "in onStop Method");
	}

	private void handleMediaPlayer() {
		if(playsWell && mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer = null;
		}
	}

	protected void onDestroy() {
		// the activity is about to be destroyed
		super.onDestroy();
		Log.d(TAG, "in onDestroy Method");
	}

	@Override
	protected void onSaveInstanceState (Bundle outState) {
		Log.d(TAG, "in onSaveInstanceState");
		onSaveInstanceState_NumCalls++;
		outState.putString(TAG, "onSaveInstanceState called " + onSaveInstanceState_NumCalls);
		outState.putInt(NUM_TAG, onSaveInstanceState_NumCalls);
	}

	public void getName(View v) {
        changeButtonPadding();
		Intent intent = new Intent(this, NameGetter.class);
		startActivityForResult(intent, GET_NAME);
	}

	public void playSound(View v) {
		if(mediaPlayer == null) {
			Log.d(TAG, "Creating Media Player");
			mediaPlayer = MediaPlayer.create(this, R.raw.light);
			mediaPlayer.setLooping(true);
		}
		if(!mediaPlayer.isPlaying()) {
			Log.d(TAG, "Starting Media Player");
			mediaPlayer.start();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG,"onActivityResult and resultCode = " + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == GET_NAME) {
			TextView t = (TextView)findViewById(R.id.text);
			t.setText(showState + "\nHi " + data.getStringExtra(NameGetter.NAME));
		}
		Log.d(TAG, "returned intent: " + data);
		Log.d(TAG, "resultCode: " + resultCode);
		Log.d(TAG, "RESULT_OKAY" + Activity.RESULT_OK);
		Log.d(TAG, "request code: " + requestCode);
	}

}