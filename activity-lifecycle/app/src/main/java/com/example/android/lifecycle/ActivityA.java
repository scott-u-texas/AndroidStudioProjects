/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.lifecycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.lifecycle.util.StatusTracker;
import com.example.android.lifecycle.util.Utils;

/**
 * Example Activity to demonstrate the lifecycle callback methods.
 */
public class ActivityA extends Activity {

    private static final String TAG = "Activity A";
    private static final String ON_PAUSE_KEY = "onPauseCounter";

    private String mActivityName;
    private TextView mStatusView;
    private TextView mStatusAllView;
    private TextView mOnPauseCountView;
    private StatusTracker mStatusTracker = StatusTracker.getInstance();
    private int mOnPauseCounter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "In onCreate method.");
        setContentView(R.layout.activity_a);
        mActivityName = getString(R.string.activity_a);
        mStatusView = (TextView)findViewById(R.id.status_view_a);
        mStatusAllView = (TextView)findViewById(R.id.status_view_all_a);
        mOnPauseCountView = (TextView) findViewById(R.id.on_pause_count_display);
        mStatusTracker.setStatus(mActivityName, getString(R.string.on_create));
        Utils.printStatus(mStatusView, mStatusAllView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "In onStart method.");
        mStatusTracker.setStatus(mActivityName, getString(R.string.on_start));
        Utils.printStatus(mStatusView, mStatusAllView);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "In onRestart method.");
        mStatusTracker.setStatus(mActivityName, getString(R.string.on_restart));
        Utils.printStatus(mStatusView, mStatusAllView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Activity A. In onResume method.");
        mStatusTracker.setStatus(mActivityName, getString(R.string.on_resume));
        Utils.printStatus(mStatusView, mStatusAllView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOnPauseCounter++;
        Log.d(TAG, "Activity A: In onPause method. onPause count = " + mOnPauseCounter);
        String onPauseMessage = "Activity A: onPause count = " + mOnPauseCounter + "\n";
        mOnPauseCountView.append(onPauseMessage);
        mStatusTracker.setStatus(mActivityName, getString(R.string.on_pause));
        Utils.printStatus(mStatusView, mStatusAllView);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        Log.d(TAG, "Activity A. In onRestoreInstanceState method.");
        mOnPauseCountView.append("Activity A: In onRestoreInstanceState method.\n");
        mOnPauseCountView.append("Activity A: onPauseCounter currently = " + mOnPauseCounter + "\n");
        mOnPauseCounter = bundle.getInt(ON_PAUSE_KEY, 0);
        mOnPauseCountView.append("Activity A: onPauseCounter reset to " + mOnPauseCounter + "\n");
    }
//
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Log.d(TAG, "Activity A. In onSaveInstanceState method.");
        mOnPauseCountView.append("Activity A: In onSaveInstanceState method." +
                "Saving counter value of " + mOnPauseCounter + "\n");
        bundle.putInt(ON_PAUSE_KEY, mOnPauseCounter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "In onStop method.");
        mStatusTracker.setStatus(mActivityName, getString(R.string.on_stop));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "In onDestroy method.");
        mStatusTracker.setStatus(mActivityName, getString(R.string.on_destroy));
        mStatusTracker.clear();
    }

    public void startDialog(View v) {
        Intent intent = new Intent(ActivityA.this, DialogActivity.class);
        startActivity(intent);
    }

    public void startActivityB(View v) {
        Intent intent = new Intent(ActivityA.this, ActivityB.class);
        startActivity(intent);
    }

    public void startActivityC(View v) {
        Intent intent = new Intent(ActivityA.this, ActivityC.class);
        startActivity(intent);
    }

    public void finishActivityA(View v) {
        ActivityA.this.finish();
    }

}
