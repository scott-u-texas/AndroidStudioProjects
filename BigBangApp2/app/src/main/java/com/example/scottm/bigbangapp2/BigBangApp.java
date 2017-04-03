package com.example.scottm.bigbangapp2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class BigBangApp extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    private static final String TAG = "TBBT";
    private static final int MIN_SENSITIVITY = 5;
    private SensorManager sensorManager;
    private LinAccListener myListener;
    private MediaPlayer soundPlayer;
    private ImageView picture;
    private int currentSensitivity;
    private SeekBar sb;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_bang_app);
        picture = (ImageView) findViewById(R.id.main_image);
        initSeekBarListener();
    }

    private void initSeekBarListener() {
        sb = (SeekBar) findViewById(R.id.seekBar1);
        currentSensitivity = (sb.getMax() / 2) + MIN_SENSITIVITY;
        sb.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // nothing to do

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // nothing to do

                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser) {
                        Log.d(TAG, "current progress: " + progress + " from user? " + fromUser);
                        currentSensitivity = progress + MIN_SENSITIVITY;
                    }
                });
    }

    private void createSensor() {
        myListener = new LinAccListener();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        sensorManager.registerListener(myListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_UI);


    }

    @Override
    public void onResume() {
        super.onResume();

        if (soundPlayer == null) {
            soundPlayer = MediaPlayer.create(this, R.raw.light_saber);
        }
        soundPlayer.setOnCompletionListener(this);
        createSensor();
        sb.setProgress(currentSensitivity);
    }

    public void onPause() {
        super.onPause();
        if (soundPlayer != null && soundPlayer.isPlaying()) {
            if (soundPlayer.isPlaying())
                soundPlayer.stop();
            soundPlayer.release();
            soundPlayer = null;
        }
        sensorManager.unregisterListener(myListener);
    }

    // regular method
    @Override
    public void onCompletion(MediaPlayer mp) {
        picture.setImageResource(R.drawable.shake);
    }

    private class LinAccListener implements SensorEventListener {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // nothing to do
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float acc = (float) Math.sqrt(x * x + y * y + z * z);
            if (acc > currentSensitivity) {
                Log.d(TAG, "x: " + x + " y: " + y + " z: " + z);
                Log.d("TAG", "acceleration: " + acc + ", currentSensitivity: " +
                        currentSensitivity);
                if (soundPlayer != null && !soundPlayer.isPlaying()) {
                    soundPlayer.start();
                    picture.setImageResource(R.drawable.light_saber);
                }
            }
        }

    }

//	// unsafe method, access network on UI thread
//	@Override
//	public void onCompletion(MediaPlayer mp) {
//		// picture.setImageResource(R.drawable.shake);
//		Bitmap b = loadImageFromNetwork("http://www.userlogos.org/files/logos/jumpordie/utexas_edu_01.png");
//		if(b != null)
//		    picture.setImageBitmap(b);
//		else
//		    picture.setImageResource(R.drawable.shake);
//	}

//
//	// safe method to access network in AsyncTask
//    @Override
//    public void onCompletion(MediaPlayer mp) {
//
//
////        // Examples below to load image from network
////        // right way
////        DownloadImageTask d = new DownloadImageTask();
////        d.execute("http://www.brandsoftheworld.com/sites/default/files/styles/logo-thumbnail/public/082010/longhorn_logo.gif");
//
//        // wrong way
//         // loadImageFromNetwork("http://www.brandsoftheworld.com/sites/default/files/styles/logo-thumbnail/public/082010/longhorn_logo.gif");
//    }
//
//    private Bitmap loadImageFromNetwork(String imageURL) {
//        Bitmap bitmap = null;
//        try {
//            URL url = new URL(imageURL);
//            bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
//        }
//        catch(IOException e) {
//            Log.d(TAG, "problem reading from url: " + imageURL + ", " + e);
//        }
//        return bitmap;
//    }
//

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {


        protected Bitmap doInBackground(String... urls) {
            return loadImageFromNetwork(urls[0]);
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null)
                picture.setImageBitmap(result);
            else
                picture.setImageResource(R.drawable.shake);
        }

        private Bitmap loadImageFromNetwork(String imageURL) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageURL);
                bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
            } catch (IOException e) {
                Log.d(TAG, "problem reading from url: " + imageURL + ", " + e);
            }
            return bitmap;
        }
    }

}