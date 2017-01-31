package com.cookbook.SMSresponser;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

// test receiving the boot up Broadcast Intent
// to spoof that Intent:
// open a command line window
// start "adb shell"
// in the adb shell type "am broadcast -a android.intent.action.BOOT_COMPLETED"
// am = activity manager
// -a = action

public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG = "StartupReceiver";
    private static final String serviceName = "com.cookbook.SMSresponser.ResponserService";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "receieved broadcast intent: " + intent.getAction());
        Log.d(TAG, "context sent: " + context);
        // startMyService(context);
    }
    
    private void startMyService(Context context) {
        Log.v(TAG,"In startMyService method");
        boolean running = isMyServiceRunning(context);
        Log.d(TAG, "running: " + running);
        if(!running) {
            try {
                // start Service
                Intent svc = new Intent(context, ResponserService.class);
                context.startService(svc);  
            }   
            catch (Exception e) {
                Log.e("onCreate", "service creation problem", e);
            }       
        }
    }
    
    private boolean isMyServiceRunning(Context context) {
        Log.v(TAG, "checking if service is running");
        ActivityManager manager 
            = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        boolean result = false;
        // log all services to demo in class
        for (RunningServiceInfo service 
                    : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.v(TAG, "Running service: " + service.service.getClassName());
            if (serviceName.equals(service.service.getClassName())) {
                result = true; 
            }
        }
        return result;
    }
}
