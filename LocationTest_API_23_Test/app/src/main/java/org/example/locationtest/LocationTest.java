/***
 * Excerpted from "Hello, Android! 3e",
 * published by The Pragmatic Bookshelf.*/
package org.example.locationtest;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LocationTest extends AppCompatActivity {

    private static final String TAG = "my Location Test";
    private static final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 17291;

    // Define human readable names
    private static final String[] ACCURACY = {"invalid", "n/a", "fine", "coarse"};
    private static final String[] POWER = {"invalid", "n/a", "low", "medium",
            "high"};
    private static final String[] STATUS = {"out of service",
            "temporarily unavailable", "available"};
    private static final String[] GPS_EVENTS = {"GPS event started", "GPS event stopped",
            "GPS event first fix", "GPS event satellite status"};

    private LocationManager mgr;
    private ScrollView scrollView;
    private TextView output;
    private GpsStatus gps;
    private ArrayList<SimpleLocationListener> mLocationListeners;
    private ToggleButton toggleButton;
    private int tryCount;
    private Location lastKnownLocation;
    private boolean locationPermissionsGranted;
    private boolean haveDumpedProviders;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mLocationListeners = new ArrayList<>();
        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        output = (TextView) findViewById(R.id.output);
        scrollView = (ScrollView) findViewById(R.id.scroll_view_1);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        Criteria criteria = new Criteria();
        String best = mgr.getBestProvider(criteria, true);
        log("\nBest provider is:   " + best);

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    public void toggleLocationProvider(View v) {
        if (toggleButton.isChecked()) {
            setListener(LocationManager.GPS_PROVIDER);
            showCurrentLocation(LocationManager.GPS_PROVIDER);
        } else {
            setListener(LocationManager.NETWORK_PROVIDER);
            showCurrentLocation(LocationManager.NETWORK_PROVIDER);
        }

    }

    public void setNearNotice(View v) {
        double gdcLat = Double.parseDouble(getString(R.string.gdc_lat));
        double gdcLong = Double.parseDouble(getString(R.string.gdc_long));
        Intent showLeavingGDCIntent = new Intent(this, LeavingGDC.class);
        int requestCode = 2008;
        PendingIntent showLeavingGDCPendingIntent
                = PendingIntent.getActivity(this,
                requestCode, showLeavingGDCIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mgr.addProximityAlert(gdcLat, gdcLong,
                    100, -1, showLeavingGDCPendingIntent);
        }
    }

    // address Button clicked, show address
    public void showAddress(View v) {
        if (lastKnownLocation != null) {
            tryCount = 0;
            getAddress();
        } else {
            output.append("\n\nNo location available. Please try again later.\n\n");
        }
    }

    private void getAddress() {
        AsyncTask<Geocoder, Void, List<Address>>
                addressFetcher = new AddFetch();
        Geocoder gc = new Geocoder(this, Locale.US);
        addressFetcher.execute(gc);
    }

    public void tryAgain() {
        tryCount++;
        if (tryCount < 5) {
            getAddress();
        } else {
            output.append("Unable to access addresses. Try again later.\n\n");
        }

    }

    // show current location on map
    public void showMap(View view) {
        if (lastKnownLocation != null) {
            // Create a Uri from an intent string.
            // // Use last known location.
            double lat = lastKnownLocation.getLatitude();
            double lng = lastKnownLocation.getLongitude();
            String locationURI = "geo:" + lat + "," + lng;
            //locationURI += "?z=5";
            Uri uriForMappingIntent = Uri.parse(locationURI);

//            locationURI = "geo:0,0?q="
//                    + lat + "," + lng
//                    + "(Current Location)";

            // Create an Intent from gmmIntentUri.
            // Set the action to ACTION_VIEW
            Intent mapIntent = new Intent(Intent.ACTION_VIEW,
                    uriForMappingIntent);

            // Make the Intent explicit by setting the Google Maps package.
            // If want to use user's preferred map app, don't do this!
            //  mapIntent.setPackage("com.google.android.apps.maps");

            // Attempt to start an activity that can handle the Intent
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }

        }
    }


    private class AddFetch extends AsyncTask<Geocoder, Void, List<Address>> {

        List<Address> addresses;

        @Override
        protected List<Address> doInBackground(Geocoder... arg0) {
            Geocoder gc = arg0[0];
            Log.d(TAG, "Geocode is present: " + Geocoder.isPresent());
            addresses = null;
//            // "forwasrd geocoding": get lat and long from name or address
//            try {
//                addresses = gc.getFromLocationName(
//                        "713 North Duchesne, St. Charles, MO", 5);
//            } catch (IOException e) {}
//            if(addresses != null && addresses.size() > 0) {
//                double lat = addresses.get(0).getLatitude();
//                double lng = addresses.get(0). getLongitude ();
//                String zip = addresses.get(0).getPostalCode();
//                Log.d(TAG, "FORWARD GEO CODING: lat: " + lat + ", long: " + lng + ", zip: " + zip);
//            }
//            Log.d(TAG, "forward geocoding address list: " + addresses);

            // also try reverse geocoding, location from lat and long
            tryReverseGeocoding(gc);
            return addresses;
        }

        private void tryReverseGeocoding(Geocoder gc) {
            double lat = lastKnownLocation.getLatitude();
            double lng = lastKnownLocation.getLongitude();
            Log.d(TAG, "REVERSE GEO CODE TEST lat: " + lat);
            Log.d(TAG, "REVERSE GEO CODE TEST long: " + lng);
            addresses = null;
            try {
                addresses = gc.getFromLocation(lat, lng, 10); // maxResults
            } catch (IOException e) {
            }
        }

        protected void onPostExecute(List<Address> result) {
            if (result == null) {
                tryAgain();
                Log.d(TAG, "\n\nNo addresses from Geocoder. Trying again. " +
                        "Try count: " + tryCount);
            } else {
                output.append("\n\nNumber of addresses " +
                        "at current location :" + addresses.size());
                output.append("\n\nBEST ADDRESS FOR CURRENT LOCATION:");
                output.append(addresses.get(0).toString());
                Log.d(TAG, "reverse geocoding, " +
                        "addresses from lat and long: "
                        + addresses.size());
                for (Address address : addresses) {
                    Log.d(TAG, address.toString());
                }
            }
        }

    }

    private void showCurrentLocation(String locationProvider) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location
                    = mgr.getLastKnownLocation(locationProvider);
            dumpLocation(location);
        }
    }

    private void setListener(String locationProvider) {
        clearListOfListeners();
        SimpleLocationListener sll = new SimpleLocationListener();
        mLocationListeners.add(sll);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mgr.requestLocationUpdates(locationProvider, 1000, 10, sll);
        }
    }

    private void clearListOfListeners() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            for (SimpleLocationListener sll : mLocationListeners)
                mgr.removeUpdates(sll);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();

        if (!locationPermissionsGranted) {
            requestLocationPermissions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // GPS OR NETWORK????
        // mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 1, locationListener);
        // provider, update in milliseconds, update in location change, listener
        // mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 200, 10, locationListener);

        //TO SEE NETWORK INFO AND STATUS
        // SimpleLocationListener sll = new SimpleLocationListener();
//        mLocationListeners.add(sll);
//        mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, sll);
//
        // TO SEE GPS INFO AND STATUS
        // SimpleLocationListener sll = new SimpleLocationListener();
        // mLocationListeners.add(sll);
        // mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, sll);
////        mgr.addGpsStatusListener(gpsStatusListener);


        if (locationPermissionsGranted && !haveDumpedProviders) {
            haveDumpedProviders = true;
            log("Location providers:");
            dumpProviders();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            log("\nLocations (starting with last known):");
            lastKnownLocation
                    = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (lastKnownLocation != null) {
                dumpLocation(lastKnownLocation);
            }

            if (toggleButton.isChecked()) {
                setListener(LocationManager.GPS_PROVIDER);
            } else {
                setListener(LocationManager.NETWORK_PROVIDER);
            }
        }
    }

    private void requestLocationPermissions() {
        log("Requesting Permission for Location");
        Log.d(TAG, "Requesting Permission for Location");
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)) {

            // Show an explanation to the user (likely with a
            // dialog) *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_GET_LOCATION);

            // MY_PERMISSIONS_REQUEST_GET_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        log("onRequestPermissionsResult");
        Log.d(TAG, "onRequestPermissionsResult");
        Log.d(TAG, "permissions: " + Arrays.toString(permissions));
        Log.d(TAG, "results: " + Arrays.toString(grantResults));
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                locationPermissionsGranted =
                        (grantResults.length > 0)
                                && (grantResults[0]
                                == PackageManager.PERMISSION_GRANTED);
            }
        }
    }

    private void checkPermissions() {
        int permissionCoarse
                = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionFine
                = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCoarse == PackageManager.PERMISSION_GRANTED) {
            log(getString(R.string.have_coarse_permission));
        } else {
            log(getString(R.string.lack_coarse_location));
        }
        if (permissionFine == PackageManager.PERMISSION_GRANTED) {
            log(getString(R.string.have_fine_permission));
        } else {
            log(getString(R.string.lack_fine_location));
        }
        locationPermissionsGranted =
                permissionCoarse == PackageManager.PERMISSION_GRANTED
                        && permissionFine == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Stop updates to save power while app paused
        clearListOfListeners();
        mgr.removeGpsStatusListener(gpsStatusListener);
    }

    private class SimpleLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            log("\n" + "onLocationChanged CALLED: ");
            if (location != null) {
                lastKnownLocation = location;
            }
            dumpLocation(location);
            Log.d("LocationTest", "Updated Location.");
        }

        public void onProviderDisabled(String provider) {
            log("\nProvider disabled: " + provider);
        }

        public void onProviderEnabled(String provider) {
            log("\nProvider enabled: " + provider);
        }

        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
            log("\nProvider status changed: " + provider + ", status="
                    + STATUS[status] + ", extras=" + extras);
        }
    }

    /**
     * Write a string to the output window
     */
    private void log(String string) {
        output.append(string + "\n");
        int height = scrollView.getChildAt(0).getHeight();
        Log.d(TAG, "scroll view height: " + height);
        scrollView.scrollTo(0, height + 2000);
    }

    /**
     * Write information from all location providers
     */
    private void dumpProviders() {
        List<String> providers = mgr.getAllProviders();
        for (String provider : providers) {
            dumpProvider(provider);
        }
    }

    /**
     * Write information from a single location provider
     */
    private void dumpProvider(String provider) {
        LocationProvider info = mgr.getProvider(provider);
        StringBuilder builder = new StringBuilder();
        builder.append("LocationProvider:")
                .append(" name=")
                .append(info.getName())
                .append("\nenabled=")
                .append(mgr.isProviderEnabled(provider))
                .append("\ngetAccuracy=")
                .append(ACCURACY[info.getAccuracy() + 1])
                .append("\ngetPowerRequirement=")
                .append(POWER[info.getPowerRequirement() + 1])
                .append("\nhasMonetaryCost=")
                .append(info.hasMonetaryCost())
                .append("\nrequiresCell=")
                .append(info.requiresCell())
                .append("\nrequiresNetwork=")
                .append(info.requiresNetwork())
                .append("\nrequiresSatellite=")
                .append(info.requiresSatellite())
                .append("\nsupportsAltitude=")
                .append(info.supportsAltitude())
                .append("\nsupportsBearing=")
                .append(info.supportsBearing())
                .append("\nsupportsSpeed=")
                .append(info.supportsSpeed())
                .append("\n\n\n");
        log(builder.toString());
    }

    /**
     * Describe the given location, which might be null
     */
    private void dumpLocation(Location location) {
        if (location == null)
            log(" ");
        else {
            log("\n" + location.toString());
        }

    }

    GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            Log.d("Location Test", "gps status changed");
            log("\n-- GPS STATUS HAS CHANGED -- " + "\n" + GPS_EVENTS[event - 1]);
            int permissionResult
                    = ActivityCompat.checkSelfPermission(LocationTest.this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionResult == PackageManager.PERMISSION_GRANTED) {
                gps = mgr.getGpsStatus(null);
            }
            showSats();
        }
    };

    private void showSats() {
        int satNum = 0;
        StringBuilder builder = new StringBuilder();
        for (GpsSatellite sat : gps.getSatellites()) {
            builder.append("Satellite Data: ");
            builder.append("\nnumber: ");
            builder.append(satNum);
            builder.append("\nAzimuth: ");
            builder.append(sat.getAzimuth());
            builder.append("\nElevation: ");
            builder.append(sat.getElevation());
            builder.append("\nSNR: ");
            builder.append(sat.getSnr());
            builder.append("\nUsed in fix?: ");
            builder.append(sat.usedInFix());
            log("\n\n" + builder.toString());
            builder.delete(0, builder.length());
            satNum++;
        }
    }

}
