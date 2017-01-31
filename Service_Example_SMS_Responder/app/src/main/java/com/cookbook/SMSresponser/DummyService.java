package com.cookbook.SMSresponser;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by scottm on 3/25/2015.
 * Based on example from http://stackoverflow.com/questions/7690350/android-start-service-on-boot
 */
public class DummyService extends Service
{
    private static final String TAG = "DummyService";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        Toast.makeText(this, "DummyService Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "DUMMY SERVICE Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
    }
}
