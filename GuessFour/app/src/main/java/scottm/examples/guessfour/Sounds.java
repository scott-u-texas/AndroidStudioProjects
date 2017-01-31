package scottm.examples.guessfour;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class Sounds {

	private static MediaPlayer thePlayer;

	private static String TAG = "Sounds";

	public static void play(Context context, int resourceID) {
		stop();
		Log.d(TAG, "trying to play sound: " + resourceID + ", context: " + context);
		try {
			thePlayer = MediaPlayer.create(context, resourceID);
			thePlayer.start();
		}
		catch(Exception e) {
			Log.d(TAG, "Enable to play sound! " + e);
		}
	}

	public static void stop() {
		if(thePlayer != null) {
			thePlayer.stop();
			thePlayer.release();
			thePlayer = null;
		}

	}
}
