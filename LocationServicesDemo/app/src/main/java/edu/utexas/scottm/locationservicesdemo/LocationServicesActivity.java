package edu.utexas.scottm.locationservicesdemo;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationServicesActivity extends Activity {

    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_services);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

//    private void createGoogleServicesApiClient() {
//        // Create an instance of GoogleApiClient.
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
//    }

//    protected void onStart() {
//        super.onStart();
//    }
//
//    protected void onStop() {
//        super.onStop();
//    }

    protected void onResume() {
        super.onResume();
        getLastKnownLocation();
    }

    private void getLastKnownLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));                        }
                    }
                });
    }

    private  class ConnectionListener
            implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
        mGoogleApiClient);
        if (mLastLocation != null) {

        }

    }
}
