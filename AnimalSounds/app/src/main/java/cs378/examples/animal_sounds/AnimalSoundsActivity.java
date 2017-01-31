package cs378.examples.animal_sounds;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class AnimalSoundsActivity extends Activity {

	private static final String TAG = "AnimalSounds";

	private GestureLibrary mLibrary;

	// for all the sounds  we play
	private SoundPool mSounds;
	private HashMap<String, Integer> mSoundIDMap;
    private HashMap<Animal, Integer> drawables;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (!mLibrary.load()) {
			finish();
		}

        GestureOverlayView overlay = (GestureOverlayView) findViewById(R.id.gestures);
		overlay.addOnGesturePerformedListener(mGestureListener);

		createSoundPool();
		createBitMaps();
	}
	
	private void createBitMaps() {
	    drawables = new HashMap<Animal, Integer>();
	    drawables.put(Animal.COW, R.drawable.cow);
	    drawables.put(Animal.PIG, R.drawable.pig);
	    drawables.put(Animal.CAT, R.drawable.cat);
    }

    @Override
	protected void onResume() {
	    super.onResume();

	    if(mSounds == null)
	        createSoundPool();
	}


	@Override
	protected void onPause() {
		super.onPause();

		if(mSounds != null) {
			mSounds.release();
			mSounds = null;
		}		
	}

	//    @Override
	//    public boolean onCreateOptionsMenu(Menu menu) {
	//        getMenuInflater().inflate(R.menu.main, menu);
	//        return true;
	//    }

	private void createSoundPool() {
		mSoundIDMap = new HashMap<String, Integer>();
		// use an array????, parallel arrays? 
		// Is there a way to extract names from gesture?
		mSounds = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		mSoundIDMap.put("Moo", mSounds.load(this, R.raw.moo, 1));
		mSoundIDMap.put("Moo2", mSounds.load(this, R.raw.moo, 1));
		mSoundIDMap.put("Meow", mSounds.load(this, R.raw.meow, 1));
		mSoundIDMap.put("Oink", mSounds.load(this, R.raw.oink, 1));
		mSoundIDMap.put("Oink2", mSounds.load(this, R.raw.oink, 1));
		mSoundIDMap.put("Oink3", mSounds.load(this, R.raw.oink, 1));
	}

	private GestureOverlayView.OnGesturePerformedListener mGestureListener 
		= new GestureOverlayView.OnGesturePerformedListener() {

		@Override
		public void onGesturePerformed(GestureOverlayView overlay,
				Gesture gesture) {
			// from http://android-developers.blogspot.com/2009/10/gestures-on-android-16.html
			
			ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
			
			// We want at least one prediction
			if (predictions.size() > 0) {
				Prediction prediction = predictions.get(0);
				
				Log.d(TAG, "prediction score: " + prediction.score + ", name: " + prediction.name);
				
				// We want at least some confidence in the result
				Animal result = null;
				if (prediction.score > 3.0) {
					String name = prediction.name;
					if(name.contains("Moo")) {
						mSounds.play(mSoundIDMap.get("Moo"), 1, 1, 1, 0, 1);
						result = Animal.COW;
					}
					else if(name.contains("Oink")) {
						mSounds.play(mSoundIDMap.get("Oink"), 1, 1, 1, 0, 1);
						result = Animal.PIG;
					}
					else if(name.contains("Meow")) {
						mSounds.play(mSoundIDMap.get("Meow"), 1, 1, 1, 0, 1);
						result = Animal.CAT;
					}
				}
				if(result != null)
				    overlay.setBackgroundResource(drawables.get(result));
				else
				    overlay.setBackgroundResource(0);
			}
		}

	};
	
	   private static enum Animal {
	        COW, PIG, CAT
	    }
}
