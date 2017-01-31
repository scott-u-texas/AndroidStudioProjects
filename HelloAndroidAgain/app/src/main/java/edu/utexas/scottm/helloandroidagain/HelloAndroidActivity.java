package edu.utexas.scottm.helloandroidagain;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class HelloAndroidActivity extends ActionBarActivity {

    private TextView tv;

    private final int REQUEST_CODE  = 6770;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_android);
        tv = (TextView) findViewById(R.id.textView);
    }

    public void checkFont(View v) {
        String font = Settings.System.FONT_SCALE;
        try {
            float fontSize = Settings.System.getFloat(getContentResolver(), font);
            tv.setText("" + fontSize);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void recordAudio(View v) {
        Intent i = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(i, REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Recording finished!", Toast.LENGTH_LONG)
                    .show();
        }
    }
}
