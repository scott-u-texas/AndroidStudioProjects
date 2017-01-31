package scottm.examples.sound;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SoundsAndVideoActivity extends Activity {

    private Vibrator vib;

    private MediaPlayer player;
    private int currentSongID;

    private static final String TAG = "Audio Demo";
    private static long[] vibrateArray = { 0, 1024, 512, 512, 256, 256, 128,
            128, 64, 64 };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        vib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        buildListeners();
        showContent();
        showRingtones();
    }

    @Override
    public void onResume() {
        super.onResume();
        // did audio get interrupted??
        SharedPreferences prefs = getSharedPreferences("sound_demo",
                MODE_PRIVATE);
        int songID = prefs.getInt("songID", -1);
        int audioLocation = prefs.getInt("audioLocation", -1);
        Log.d(TAG, "in onCreate - song id: " + songID);
        Log.d(TAG, "in onCreate - location: " + audioLocation);
        if (songID != -1) {
            // int audioLocation = prefs.getInt("audioLocation", 0);

            Log.d(TAG, "in onCreate in if song id: " + songID);
            Log.d(TAG, "location: " + audioLocation);
            playSound(songID, audioLocation);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "in on save instance state");
        // stopPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "in on pause");
        stopPlayer();
    }

    public void playFromURL(View v) {
        // String url = "http://www.pacdv.com/sounds/" +
        // "machine_sound_effects/chain-saw-2.mp3";

        // String url =
        // "http://upload.wikimedia.org/wikipedia/en/b/b5/Radiohead_-_Pyramid_Song_%28sample%29.ogg";
        stopPlayer();
        String url = "http://www.pacdv.com/sounds/machine_sound_effects/chain-saw-2.mp3"; // your
                                                                                          // URL
                                                                                          // here
        if (player == null)
            player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(url);
            player.prepareAsync();
            // You can show progress dialog here until it prepared to play
            player.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // Now dismiss progress dialog, Media player will start
                    // playing
                    mp.start();
                }
            });
            player.setOnErrorListener(new OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // dismiss progress bar here. It will come here when
                    // MediaPlayer
                    // is not able to play file. You can show error message to
                    // user
                    return false;
                }
            });
        } catch (IOException e) {
            Log.d(TAG, "Error in trying to play from URL on network: " + e);
            for (StackTraceElement elem : e.getStackTrace())
                Log.d(TAG, elem.toString());
            if (player != null) {
                if (player.isPlaying()) {
                    player.stop();
                }
                player.release();
                player = null;
            }
        }
        // new MediaPlayerFromURL().execute(url);
    }

    // private class MediaPlayerFromURL extends AsyncTask<String, Void,
    // MediaPlayer> {
    //
    // @Override
    // protected MediaPlayer doInBackground(String... url) {
    // if(player == null)
    // player = new MediaPlayer();
    // player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    // try {
    // player.setDataSource(url[0]);
    // player.prepare(); // might take long! (for buffering, etc)
    // }
    // catch (IOException e){
    // Log.d(TAG, "Error in trying to play from URL on network: " + e);
    // if(player != null) {
    // if(player.isPlaying()) {
    // player.stop();
    // }
    // player.release();
    // player = null;
    // }
    // }
    // return player;
    // }
    //
    // protected void onPostExecute(MediaPlayer result) {
    // if(player != null)
    // player.start();
    // }
    //
    // }

    private Uri showRingtones() {
        Uri result = null;
        RingtoneManager rm = new RingtoneManager(this);
        rm.setType(RingtoneManager.TYPE_ALL);
        Cursor cursor = rm.getCursor();
        if (cursor == null) {
            Log.d(TAG, "cursor == null, query failed");
        } else if (!cursor.moveToFirst()) {
            Log.d(TAG, "no ringtones on the device");
        } else {
            int count = cursor.getCount();
            Log.d(TAG, "count of ringtones: " + count);
            for (int i = 0; i < count; i++) {
                Ringtone r = rm.getRingtone(i);
                Log.d(TAG, "ringtone num: " + i + " name: " + r.getTitle(this));
            }
            int num = (int) (Math.random() * count);
            result = rm.getRingtoneUri(num);
        }
        return result;
    }

    private void playRandomRingtone() {
        Uri uri = showRingtones();
        if (uri != null) {
            stopPlayer();
            if (player == null) {
                player = new MediaPlayer();
            }
            try {
                player.setDataSource(this, uri);
                player.prepare();
                // player.setLooping(true);
                player.start();
            } catch (IOException e) {
                Log.d(TAG, "Error in trying to play random song: " + e);
                if (player != null) {
                    if (player.isPlaying()) {
                        player.stop();
                    }
                    player.release();
                    player = null;
                }
            }
        }
    }

    private long showContent() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        long result = -1;
        if (cursor == null) {
            Log.d(TAG, "cursor == null, query falied");
        } else if (!cursor.moveToFirst()) {
            Log.d(TAG, "no media on the device");
        } else {
            ArrayList<Long> ids = new ArrayList<Long>();
            int titleColumn = cursor
                    .getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = cursor
                    .getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            do {
                long thisId = cursor.getLong(idColumn);
                ids.add(thisId);
                String thisTitle = cursor.getString(titleColumn);
                Log.d(TAG, "found media: thisID: " + thisId + ", thisTitle: "
                        + thisTitle);
            } while (cursor.moveToNext());
            Collections.shuffle(ids);
            Log.d(TAG, ids.size() + "");
            result = ids.get(0);
            Log.d(TAG, "picked this id at random: " + ids.get(0));
        }
        return result;
    }

    private void buildListeners() {
        int[] ids = { R.id.gong, R.id.ava, R.id.fax, R.id.folk, R.id.rise,
                R.id.rain };
        int[] songs = { R.raw.gong, R.raw.ava_maria, R.raw.fax, R.raw.music,
                R.raw.rise, R.raw.rain };

        for (int i = 0; i < ids.length; i++) {
            Button button = (Button) findViewById(ids[i]);
            final int SONG_ID = songs[i];
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    playSound(SONG_ID, 0);
                }
            });
        }

        initRandomButton();
        initStopButton();
    }

    private void initRandomButton() {
        Button random = (Button) findViewById(R.id.random);
        random.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // showRingtones();
                // playRandomRingtone();
                playRandomSong();
                // playFromURL();
            }
        });

    }

    private void playRandomSong() {
        stopPlayer();

        // get id of random song
        long id = showContent();

        Uri contentUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                id);

        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
                player.release();
            }
            player = null;
        }

        player = MediaPlayer.create(this, contentUri);
        player.start();

        // alternative to MediaPlayer.create(),
        // if(player == null) {
        // player = new MediaPlayer();
        // }

        // try {
        // player.setDataSource(this, contentUri);
        // player.prepare();
        // player.start();
        // }
        // catch (IOException e){
        // Log.d(TAG, "Error in trying to play random song: " + e);
        // if(player != null) {
        // if(player.isPlaying()) {
        // player.stop();
        // }
        // player.release();
        // player = null;
        // }
        // }
    }

    private void initStopButton() {
        // set up the stop button
        Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vib.vibrate(200); // milliseconds
                // vib.vibrate(vibrateArray, -1);
                stopPlayer();
            }
        });
    }

    // private void stopPlayer() {
    // if(player != null) {
    // player.stop();
    // player.release();
    // player = null;
    // }
    // }

    private void stopPlayer() {
        SharedPreferences mPrefs = getSharedPreferences("sound_demo",
                MODE_PRIVATE);
        SharedPreferences.Editor ed = mPrefs.edit();
        // need to fix this, not working if playing sound not from res/raw
        if (player != null) {
            if (player.isPlaying()) {
                ed.putInt("songID", currentSongID);
                ed.putInt("audioLocation", player.getCurrentPosition());
                Log.d(TAG, "in stopPlayer - song id: " + currentSongID);
                Log.d(TAG,
                        "in stopPlayer - audio Location: "
                                + player.getCurrentPosition());
            } else {
                ed.putInt("songID", -1);
            }
            player.stop();
            player.release();
            player = null;
        } else {
            ed.putInt("songID", -1);
        }
        ed.apply();
    }

    private void playSound(int songID, int location) {
        if (player == null || !player.isPlaying()) {
            Log.d(TAG, "in playSound - player null or not playing "
                    + "- creating new player");
            Log.d(TAG, "in playSound - songID: " + songID);
            player = MediaPlayer.create(this, songID);
            if (location != 0) {
                player.seekTo(location);
            }
        }
        if (player.isPlaying()) {
            Log.d(TAG, "player playing - " + "stopping and releasing");
            player.stop();
            player.release();
            player = MediaPlayer.create(this, songID);

        }
        currentSongID = songID;
        player.start();
    }

    // public void playSound(int songID) {
    // // simplest approach
    //
    // MediaPlayer mp = MediaPlayer.create(this, songID);
    // player.start();
    // // no need to call prepare(); create() does that for you
    // }

}