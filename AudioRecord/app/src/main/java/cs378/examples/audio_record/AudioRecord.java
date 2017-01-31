package cs378.examples.audio_record;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class AudioRecord extends Activity {
	
	private static final String TAG = "Audio Record";
	
	private MediaRecorder audioRecorder;
	private final String RECORD_FILE = "/audio_record";
    private Button recordButton;
    private int oldColor = -1;

	private boolean recording;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d(TAG, Build.DEVICE);
        recordButton = (Button) findViewById(R.id.start_record);
    }

    
    public void recordAudio(View v) {
		if (!recording) {
			recording = true;
            recordButton.setText("** RECORDING **");
            if(audioRecorder == null)
                audioRecorder = new MediaRecorder();
            String pathForAudioRecording = getFilesDir().getAbsolutePath();
            pathForAudioRecording += RECORD_FILE;

            audioRecorder.setAudioSource(
                    MediaRecorder.AudioSource.MIC);
            audioRecorder.setOutputFormat(
                    MediaRecorder.OutputFormat.DEFAULT);
            audioRecorder.setAudioEncoder(
                    MediaRecorder.AudioEncoder.DEFAULT);

            audioRecorder.setOutputFile(pathForAudioRecording);

            try {
                audioRecorder.prepare();
                audioRecorder.start();
            }
            catch(IOException e) {
                Log.e(TAG, "unable to record. could not prepare or start MediaRecorder");
            }
		}
    }
    
    
    public void stopRecording(View v) {
        if (recording) {
            recording = false;
            recordButton.setText(getString(R.string.record_audio));
            if (audioRecorder != null) {
                audioRecorder.stop();
                audioRecorder.release();
                audioRecorder = null;
            }
        }
    }
    
    
    public void playAudio(View v) { 
    	MediaPlayer player = new MediaPlayer();
    	try {
    		String audioFilePath = getFilesDir().getAbsolutePath();
    		audioFilePath += RECORD_FILE;
    		
    		Log.d(TAG, "path to audio: " + audioFilePath);
    		
    		player.setDataSource(audioFilePath);
    		
    		player.prepare();
    		player.start();
    	}
    	catch(IOException e) {
    		Log.d(TAG, "Error in trying to play audio: " + e);
    	}
    }
}


///*
// * The application needs to have the permission to write to external storage
// * if the output file is written to the external storage, and also the
// * permission to record audio. These permissions must be set in the
// * application's AndroidManifest.xml file, with something like:
// *
// * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
// * <uses-permission android:name="android.permission.RECORD_AUDIO" />
// *
// */
//package cs378.examples.audio_record;
//
//import android.app.Activity;
//import android.widget.LinearLayout;
//import android.os.Bundle;
//import android.os.Environment;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.content.Context;
//import android.util.Log;
//import android.media.MediaRecorder;
//import android.media.MediaPlayer;
//
//import java.io.IOException;
//
//
//public class AudioRecord extends Activity
//{
//    private static final String LOG_TAG = "AudioRecordTest";
//    private static String mFileName = null;
//
//    private RecordButton mRecordButton = null;
//    private MediaRecorder mRecorder = null;
//
//    private PlayButton   mPlayButton = null;
//    private MediaPlayer   mPlayer = null;
//
//    private void onRecord(boolean start) {
//        if (start) {
//            startRecording();
//        } else {
//            stopRecording();
//        }
//    }
//
//    private void onPlay(boolean start) {
//        if (start) {
//            startPlaying();
//        } else {
//            stopPlaying();
//        }
//    }
//
//    private void startPlaying() {
//        mPlayer = new MediaPlayer();
//        try {
//            mPlayer.setDataSource(mFileName);
//            mPlayer.prepare();
//            mPlayer.start();
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
//        }
//    }
//
//    private void stopPlaying() {
//        mPlayer.release();
//        mPlayer = null;
//    }
//
//    private void startRecording() {
//        mRecorder = new MediaRecorder();
//        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mRecorder.setOutputFile(mFileName);
//        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//
//        try {
//            mRecorder.prepare();
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
//        }
//
//        mRecorder.start();
//    }
//
//    private void stopRecording() {
//        mRecorder.stop();
//        mRecorder.release();
//        mRecorder = null;
//    }
//
//    class RecordButton extends Button {
//        boolean mStartRecording = true;
//
//        OnClickListener clicker = new OnClickListener() {
//            public void onClick(View v) {
//                onRecord(mStartRecording);
//                if (mStartRecording) {
//                    setText("Stop recording");
//                } else {
//                    setText("Start recording");
//                }
//                mStartRecording = !mStartRecording;
//            }
//        };
//
//        public RecordButton(Context ctx) {
//            super(ctx);
//            setText("Start recording");
//            setOnClickListener(clicker);
//        }
//    }
//
//    class PlayButton extends Button {
//        boolean mStartPlaying = true;
//
//        OnClickListener clicker = new OnClickListener() {
//            public void onClick(View v) {
//                onPlay(mStartPlaying);
//                if (mStartPlaying) {
//                    setText("Stop playing");
//                } else {
//                    setText("Start playing");
//                }
//                mStartPlaying = !mStartPlaying;
//            }
//        };
//
//        public PlayButton(Context ctx) {
//            super(ctx);
//            setText("Start playing");
//            setOnClickListener(clicker);
//        }
//    }
//
//    public AudioRecord() {
//        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
//        mFileName += "/audiorecordtest.3gp";
//    }
//
//    @Override
//    public void onCreate(Bundle icicle) {
//        super.onCreate(icicle);
//
//        LinearLayout ll = new LinearLayout(this);
//        mRecordButton = new RecordButton(this);
//        ll.addView(mRecordButton,
//            new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                0));
//        mPlayButton = new PlayButton(this);
//        ll.addView(mPlayButton,
//            new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                0));
//        setContentView(ll);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (mRecorder != null) {
//            mRecorder.release();
//            mRecorder = null;
//        }
//
//        if (mPlayer != null) {
//            mPlayer.release();
//            mPlayer = null;
//        }
//    }
//}