package scottm.examples;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class HelloMapViewSample extends MapActivity {

    private static final String TAG = "HelloMapView";

    private MapView mapView;
    private List<Overlay> mapOverlays;
    private Drawable drawable;
    private HelloItemizedOverlay itemizedOverlay;
    private List<Address> addresses;
    int tryCount = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main); 
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        // set zoom
        mapView.getController().setZoom(3);

        mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.longhorn);
        itemizedOverlay = new HelloItemizedOverlay(drawable, this);
        mapOverlays.add(itemizedOverlay);
//
//        
//        
        tryGeoCoder();
    }



    // complete on a separate thread as this is a blocking operation on
    // the network
    
    private void tryGeoCoder() {
        AsyncTask<Geocoder, Void, List<Address>> addressFetcher = new AddFetch();
        Geocoder gc = new Geocoder(this, Locale.US);
        addressFetcher.execute(gc);
    }

    public void tryAgain() {
        tryCount++;
        if(tryCount < 10) {
            AsyncTask<Geocoder, Void, List<Address>> addressFetcher = new AddFetch();
            Geocoder gc = new Geocoder(this, Locale.US);
            addressFetcher.execute(gc);
        }

    }

    
    private  class AddFetch extends AsyncTask<Geocoder, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(Geocoder... arg0) {
            Geocoder gc = arg0[0];
            Log.d(TAG, "Geocode is present: " + Geocoder.isPresent());
            addresses = null;
            // "forward geocoding": get lat and long from name or address
            try {
                addresses = gc.getFromLocationName(
                        "713 North Duchesne, St. Charles, MO", 5);
            } catch (IOException e) {}
            if(addresses != null && addresses.size() > 0) {
                double lat = addresses.get(0).getLatitude();
                double lng = addresses.get(0). getLongitude ();
                String zip = addresses.get(0).getPostalCode();
                Log.d(TAG, "FORWARD GEO CODING: lat: " + lat + ", long: " + lng + ", zip: " + zip);
            }
            Log.d(TAG, "forward geocoding address list: " + addresses);
            
            // also try reverse geocoding, location from lat and long
            tryReverseGeocoding(gc);
            return addresses;
        }

        private void tryReverseGeocoding(Geocoder gc) {
            LocationManager locMgr = ((LocationManager) getSystemService(LOCATION_SERVICE));
            Location location = locMgr.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            double lat = location.getLatitude() ;
            double lng = location.getLongitude();
            Log.d(TAG, "REVERSE GEO CODE TEST lat: " + lat);
            Log.d(TAG, "REVERSE GEO CODE TEST long: " + lng);
            List<Address> addresses = null;
            try {
              addresses = gc.getFromLocation(lat, lng, 20); // maxResults
            } catch (IOException e) {}
            if(addresses != null)
                Log.d(TAG, "reverse geocoding, addresses from lat and long: " + addresses + " " + addresses.size());
        }

        protected void onPostExecute(List<Address> result) {
            if(result == null)
                tryAgain();
        }

    }


    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}