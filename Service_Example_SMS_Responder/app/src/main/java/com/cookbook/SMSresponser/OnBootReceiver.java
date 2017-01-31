package com.cookbook.SMSresponser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("OnBootReceiver", "Boot broadcast received!");
        Intent in = new Intent(context, DummyService.class);
        context.startService(in);
    }
}