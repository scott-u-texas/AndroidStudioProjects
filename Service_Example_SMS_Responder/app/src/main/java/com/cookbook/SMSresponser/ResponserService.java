package com.cookbook.SMSresponser;

import android.app.Activity;
import android.app.Service;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class ResponserService extends Service {

	private static final String TAG = "SMS-Response-Service";
	
	//The Broadcast Intent
	// sent by the Android system 
	// when a SMS is received.
	private static final String RECEIVED_ACTION 
			= Telephony.Sms.Intents.SMS_RECEIVED_ACTION;

    // These are used in our own, home grown intents that
    // will be broadcast when our automatic response
    // is sent and delivered.
	private static final String SENT_ACTION = "SMS_SENT_BY_AUTO_RESPONDER";


	//private static final String DELIVERED_ACTION = "DELIVERED_SMS";

	private String requester;
    private SharedPreferences myprefs;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "In onCreate for ResponserService class");
        myprefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        // sentReceiver informed when message from our app sent
	    registerReceiver(sentReceiver, new IntentFilter(SENT_ACTION));
	    
//	    // deliverReceiver informed when message from our app delivered
//	    registerReceiver(deliverReceiver, new IntentFilter(DELIVERED_ACTION));
       
	    // receiver is the Broadcast receiver that actually responds to
	    // incoming SMS messages
	    IntentFilter intentFilter = new IntentFilter(RECEIVED_ACTION);
        registerReceiver(receiver, intentFilter);
        
        // alternate Broadcast Receiver for knowing when message sent
//        IntentFilter sendfilter = new IntentFilter(SENT_ACTION);
//        registerReceiver(sender, sendfilter);
	}
	
	
	// alternate Broadcast Receiver for knowing when message sent
//    private BroadcastReceiver sender = new BroadcastReceiver() {
//    	@Override
//    	public void onReceive(Context c, Intent i) {
//    	    Log.d(TAG, "in onReceive method of sender");
//    		if(i.getAction().equals(SENT_ACTION)) {
//    		
//    			if(getResultCode() != Activity.RESULT_OK) {
//    				String recipient = i.getStringExtra("recipient");
//    				requestReceived(recipient);
//    			}
//    		}
//    	}
//    };
    
	
    private BroadcastReceiver sentReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context c, Intent in) {
        	Log.d(TAG, "in onReceive method of sentReceiver");
        	Log.d(TAG, "result code: " + getResultCode());
        	Log.d(TAG, "result code equals Activity.RESULT_OK " + (getResultCode() == Activity.RESULT_OK));
        	Log.d(TAG, "Context: " + c);
        	Log.d(TAG, "Intent: " + in);
        	if(getResultCode() == Activity.RESULT_OK
                    && in.getAction().equals(SENT_ACTION)) {
                // SMS Sent was from our app
        	    Log.d(TAG, "Activity result ok. Our apps SMS sent.");
        	    smsSent();
        	}
        	else {
        	    Log.d(TAG, "Either result not okay, or SMS sent was not from our app");
        	    smsFailed();
        	}
         }
    };
    
    public void smsSent(){
    	Toast.makeText(this, 
    	        "Auto Responding to message. SMS sent", 
    	        Toast.LENGTH_SHORT).show();
    }
    
    public void smsFailed(){
    	Toast.makeText(this, "Auto respond SMS sent failed", Toast.LENGTH_SHORT).show();
    }
//
//    public void smsDelivered(){
//        Log.d(TAG, "in smsDelivered method");
//    	Toast.makeText(this, "Auto respond SMS delivered", Toast.LENGTH_LONG).show();
//    }
    
//   private BroadcastReceiver deliverReceiver = new BroadcastReceiver() {
//       // this is never getting called!!!
//
//        @Override public void onReceive(Context c, Intent in) {
//            //SMS delivered actions
//            Log.d(TAG, "in onReceive method of deliverReceiver");
//        	smsDelivered();
//        }
//    };
    
    public void requestReceived(String f) {
    	Log.v(TAG,"In requestReceived. value of f: " + f);
    	requester = f;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context c, Intent in) {
    	    Log.d(TAG, "in onReceive method of receiver");
    		if(in.getAction().equals(RECEIVED_ACTION)) {
        		Log.v(TAG,"On SMS RECEIVE");

    			Bundle bundle = in.getExtras();
    			if(bundle != null) {

    				Object[] pdus = (Object[])bundle.get("pdus");
    				SmsMessage[] messages = new SmsMessage[pdus.length];
    				Log.v(TAG, "" + messages.length);
    				for(int i = 0; i<pdus.length; i++) {
    		    		Log.v(TAG,"FOUND MESSAGE");
    					messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
    					Log.d(TAG, messages[i].getDisplayMessageBody());
    				}
    				for(SmsMessage message: messages) {
    					requestReceived(message.getOriginatingAddress());
    				}
    				respond();
    			}
    		}

    	}
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.d(TAG, "in onStart Command for ResponserService");
		return START_STICKY;
    }

    private void respond() {
        String reply = myprefs.getString("reply", "Thank you for your message. I am busy now. I will call you later");
    	if(reply.length() == 0)
    		reply = "Thank you for your message. I am busy now. I will call you later";
        SmsManager sms = SmsManager.getDefault();
    	Intent sentIntent = new Intent(SENT_ACTION);

    	PendingIntent sentPendingIntent
    	    = PendingIntent.getBroadcast(this, 0, sentIntent, 0);

    	if(reply.length() > 140)
    		reply = reply.substring(0, 140);
       
        sms.sendTextMessage(requester, null, reply, sentPendingIntent, null);
        // parameters are: destination address, service center address(null for default),
        // message, pending intent to be broadcast when message sent,
        // pending intent to be broadcast when message delivered



        //     	Intent deliverIntent = new Intent(DELIVERED_ACTION);
//        PendingIntent deliverPendingIntent
//                = PendingIntent.getBroadcast(this, 0, deliverIntent, 0);
    }


	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "in onDestroy");
		unregisterReceiver(receiver);
		unregisterReceiver(sentReceiver);


		// unregisterReceiver(deliverReceiver);
        // unregisterReceiver(sender);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
