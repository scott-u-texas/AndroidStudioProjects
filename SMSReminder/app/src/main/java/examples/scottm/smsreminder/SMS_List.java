package examples.scottm.smsreminder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

public class SMS_List extends AppCompatActivity {

    private static final String TAG = "SMS_List";

    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 8292;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1728;
    private static final int GET_SINGLE_CONTACT_REQUEST = 3317;
    private static final int GET_MULTIPLE_CONTACT_REQUEST = 7136;

    private boolean smsPermission;
    private TextView contactInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms__list);
        contactInfo = (TextView) findViewById(R.id.contact_info);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS);
        Log.d(TAG, "in onResume: permissionCheck: " + permissionCheck + ", permission granted = " +
                PackageManager.PERMISSION_GRANTED);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "we don't have permission.");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_SMS)) {
                Log.d(TAG, "explanation needed.");
                showExplanation("Permission Needed", "Permission needed to read SMS data", Manifest.permission.READ_SMS, MY_PERMISSIONS_REQUEST_READ_SMS);
            } else {
                Log.d(TAG, "No explanation needed: requesting permission from onResume");
                requestPermission(Manifest.permission.READ_SMS, MY_PERMISSIONS_REQUEST_READ_SMS);
            }

        } else {
            smsPermission = true;
            Log.d(TAG, "We already have permission. Calling show columns from onResume");
            showSMSSentColumns();
        }

    }

    /*
                    ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS},
                        MY_PERMISSIONS_REQUEST_READ_SMS);
     */

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        Log.d(TAG, "showing explanation for permission.");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        Log.d(TAG, "calling request permission in ActivityCompat");
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    private void showSMSSentColumns() {
        Cursor c = getContentResolver().query(Telephony.Sms.Sent.CONTENT_URI, null, null, null, null);
        if (c != null) {
            Log.d(TAG, "SMS Sent Columns");
            for (String s : c.getColumnNames()) {
                Log.d(TAG, "Column: " + s);
            }
            c.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "in on onRequestPermissionsResult");
        Log.d(TAG, "request code: " + requestCode );
        Log.d(TAG, "permissions: " + Arrays.toString(permissions) );
        Log.d(TAG, "grant results: " + Arrays.toString(grantResults) );
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    smsPermission = true;
                    Log.d(TAG, "calling show columns from onRequestPermissionsResult");
                    showSMSSentColumns();
                    break;
                } // end if
            } // end case MY_PERMISSIONS_REQUEST_READ_SMS:
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "getting single contact");
                    getSingleContact();
                    break;
                } // end if
            } // end case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
            // other 'case' lines to check for other
            // permissions this app might request
        } // end switch
    }

    public void getSingleContact(View v) {
        Log.d(TAG, "in get single contact: ");
        if (checkAndRequestPermission(Manifest.permission.READ_CONTACTS, MY_PERMISSIONS_REQUEST_READ_CONTACTS)) {
            getSingleContact();
        };
    }

    private void getSingleContact() {
        Log.d(TAG, "getting single contact info");
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, GET_SINGLE_CONTACT_REQUEST );
    }

    private boolean checkAndRequestPermission(String permissionName, int permissionRequestCode) {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                permissionName);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "we don't have permission.");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permissionName)) {
                Log.d(TAG, "explanation needed.");
                showExplanation("Permission Needed", "Permission needed", permissionName, permissionRequestCode);
            } else {
                Log.d(TAG, "No explanation needed: requesting permission from onResume");
                requestPermission(permissionName, permissionRequestCode);
            }
        }
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "in on onActivityResult");
        // Check which request we're responding to
        if (requestCode == GET_SINGLE_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                Log.d(TAG, "uri from data: " + contactData);
                String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

                Cursor c = getContentResolver().query(contactData, projection, null, null, null);
                if (c.moveToFirst()) {

                    // Retrieve the phone number from the NUMBER column
                    int phoneNumberColumn = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int nameColumn = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    String phoneNumber = c.getString(phoneNumberColumn);
                    String name = c.getString(nameColumn);
                    contactInfo.append("\nName: " + name);
                    contactInfo.append("\nNumber" + phoneNumber);
//                    String phoneNumber = "NO PHONE NUMBER";
//                    String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
//                    Log.d(TAG, "id: " + id);
//                    String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//                    Log.d(TAG, "has phone: " + hasPhone);
//                    if (hasPhone.equalsIgnoreCase("1")) {
//                        Cursor phones = getContentResolver().query(
//                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
//                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
//                                null, null);
//                        phones.moveToFirst();
//                        phoneNumber = phones.getString(phones.getColumnIndex("data1"));
//                        phones.close();
//                    }
//                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                    contactInfo.append("\n" + name);
//                    contactInfo.append("\n" + phoneNumber);
                }
                c.close();
            }
        }
    }

    public void getMultipleContact(View view) {
        //TODO
        // CHECK PERMISSION GRANTED!!
        Intent n = new Intent(this, GetMultipleContactActivity.class);
        startActivityForResult(n, GET_MULTIPLE_CONTACT_REQUEST);
    }
}