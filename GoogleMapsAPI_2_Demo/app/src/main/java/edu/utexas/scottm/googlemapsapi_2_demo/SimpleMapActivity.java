package edu.utexas.scottm.googlemapsapi_2_demo;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class SimpleMapActivity extends Activity {

    static final LatLng AUSTIN = new LatLng(30.262, -97.745);
    static final LatLng ARLINGTON = new LatLng(32.751, -97.083);
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_map);
        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map))
                .getMap();

        if (map != null) {
            Marker austin
                    = map.addMarker(new MarkerOptions()
                    .position(AUSTIN)
                    .title("Austin")
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.longhorn)));

            Marker arlington = map.addMarker(new MarkerOptions()
                    .position(ARLINGTON)
                    .title("Arlington")
                    .snippet("Play Ball!!")
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_launcher)));

            CameraUpdate center=
                    CameraUpdateFactory.newLatLng(AUSTIN);
            CameraUpdate zoom=CameraUpdateFactory.zoomTo(5);

            map.moveCamera(center);
            map.animateCamera(zoom);
        }

    }
}
