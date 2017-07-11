package com.cookbook.SMSresponser;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;

public class SMSResponser extends AppCompatActivity {

	private static final String serviceName = "com.cookbook.SMSresponser.ResponserService";
	private static final String TAG = "SMS App";
	private static final int MY_PERMISSIONS_REQUEST_SMS = 25627;

	private TextView tv1;
	private EditText ed1;
	private SharedPreferences myPrefs;
	private String reply;

	private boolean SMS_Permission;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG,"In onCreate method");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		myPrefs 
		    = PreferenceManager.getDefaultSharedPreferences(this);
		tv1 = (TextView) this.findViewById(R.id.display);
		ed1 = (EditText) this.findViewById(R.id.editText);      
	}

    @Override
	protected void onStart() {
		super.onStart();
		checkPermissions();
        Log.d(TAG, "in onStart. Checking Permission.");
        Log.d(TAG, "Status of SMS Permission: " + SMS_Permission);
		if (!SMS_Permission) {
			requestSMSPermissions();
		}
	}

	private void requestSMSPermissions() {
		Log.d(TAG, "Requesting Permission for SMS");
		// Should we show an explanation?
		if (ActivityCompat.shouldShowRequestPermissionRationale(this,
				Manifest.permission.RECEIVE_SMS)) {

			// Show an explanation to the user (likely with a
			// dialog) *asynchronously* -- don't block
			// this thread waiting for the user's response! After the user
			// sees the explanation, try again to request the permission.

		} else {
			// No explanation needed, we can request the permission.
			ActivityCompat.requestPermissions(this,
					new String[]{
							Manifest.permission.RECEIVE_SMS,
							Manifest.permission.SEND_SMS,
							Manifest.permission.READ_SMS},
					MY_PERMISSIONS_REQUEST_SMS);

			// MY_PERMISSIONS_REQUEST_GET_LOCATION is an
			// app-defined int constant. The callback method gets the
			// result of the request.
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[],
										   int[] grantResults) {
		Log.d(TAG, "onRequestPermissionsResult");
		Log.d(TAG, "permissions: " + Arrays.toString(permissions));
		Log.d(TAG, "results: " + Arrays.toString(grantResults));
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_SMS : {
				// If request is cancelled, the result arrays are empty.
                SMS_Permission =
						(grantResults.length > 0)
								&& (grantResults[0]
								== PackageManager.PERMISSION_GRANTED);
			}
		}
	}

	private void checkPermissions() {
        SMS_Permission = true;
		int permissionSendSMS
				= ContextCompat.checkSelfPermission(this,
				Manifest.permission.SEND_SMS);
		int permissionReceiveSMS
                = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        int permissionReadSMS
                = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS);
		if (permissionSendSMS == PackageManager.PERMISSION_GRANTED) {
			Log.d(TAG, "We have permission to send SMS");
		} else {
            SMS_Permission = false;
            Log.d(TAG, "We DO NOT have permission to send SMS");
		}
		if (permissionReceiveSMS == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "We have permission to receive SMS");
        } else {
            SMS_Permission = false;
            Log.d(TAG, "We DO NOT have permissiont o to receive SMS");
		}
        if (permissionReadSMS == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "We have permission to read SMS");
        } else {
            SMS_Permission = false;
            Log.d(TAG, "We DO NOT have permissionto to read SMS");
        }



	}

	private void startMyService() {
		Log.v(TAG,"In startMyService method");
		boolean running = isMyServiceRunning();
		Log.d(TAG, "running: " + running);
		if(!running) {
			try {

////For requesting permission.
//                // Here, thisActivity is the current activity
//                if (ContextCompat.checkSelfPermission(this,
//                        Manifest.permission.READ_CONTACTS)
//                        != PackageManager.PERMISSION_GRANTED) {
//
//                    // Should we show an explanation?
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                            Manifest.permission.RECEIVE_SMS)) {
//
//                        // Show an explanation to the user *asynchronously* -- don't block
//                        // this thread waiting for the user's response! After the user
//                        // sees the explanation, try again to request the permission.
//
//                    } else {
//
//                        // No explanation needed, we can request the permission.
//
//                        ActivityCompat.requestPermissions(this,
//                                new String[]{Manifest.permission.READ_CONTACTS},
//                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//
//                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                        // app-defined int constant. The callback method gets the
//                        // result of the request.
//                    }
//                }


				// start Service
				Intent svc = new Intent(this, ResponserService.class);
                startService(svc);
            }
			catch (Exception e) {
				Log.e("onCreate", "service creation problem", e);
			}    	
		}
	}


	public void onResume() {
		super.onResume();
		Log.v(TAG, "in onResume");
		reply = myPrefs.getString("reply", "Thank you " +
				"for your message. " +
				"I am busy now. I will call you later");
		tv1.setText(reply);
		ed1.setHint(reply);
		// startMyService();
	}
	
	public void changeAutoReplyText(View v) {
		Log.v(TAG, "in change respond text");
		reply = ed1.getText().toString();
		if(reply.length() == 0)
			reply = "Thank you for your message. " +
					"I am busy now. " + 
					"I will call you later.";
		tv1.setText(reply);
        SharedPreferences.Editor updater = myPrefs.edit();
		updater.putString("reply", reply);
        updater.apply();
	}

	public void startAutoRespond(View v) {
		Log.v(TAG, "in start auto respond ");
		startMyService();
	}

	private void attemptToSendTimeZoneChange() {
        // demonstrate that trying to send a protected broadcast intent
        // results in shutdown of app
		Intent timeZoneChange =
                new Intent(Intent.ACTION_TIMEZONE_CHANGED);
		sendBroadcast(timeZoneChange);
	}

	public void stopAutoRespond(View v) {
		Log.d(TAG, "in stop auto respond");
		Intent svc = new Intent(this, ResponserService.class);
		boolean stopResult = stopService(svc);	
		Log.d(TAG, "stop service result: " + stopResult);
	}

	private boolean isMyServiceRunning() {
		Log.v(TAG, "checking if service is running");
	    ActivityManager manager 
	    	= (ActivityManager) getSystemService(ACTIVITY_SERVICE);
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