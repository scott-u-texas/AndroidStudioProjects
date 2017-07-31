package edu.utexas.scottm.googlemapsapi_2_demo;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class SimpleMapActivity extends Activity  {

    static final LatLng AUSTIN = new LatLng(30.262, -97.745);
    static final LatLng ARLINGTON = new LatLng(32.751, -97.083);
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_map);
        MapFragment mapFrag =
                ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map));

        mapFrag.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        map = googleMap;
                        setMarkers();
                        moveCamera();
                    }
                });
    }

    private void setMarkers() {
        map.addMarker(new MarkerOptions()
                .position(AUSTIN)
                .title("Austin"));
//                .icon(BitmapDescriptorFactory
//                        .fromResource(R.drawable.longhorn)));

        map.addMarker(new MarkerOptions()
                .position(ARLINGTON)
                .title("Arlington")
                .snippet("Play Ball!!")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_launcher)));
    }

    private void moveCamera() {
        CameraUpdate centerAndZoom =
                CameraUpdateFactory.newLatLngZoom(AUSTIN, 5);
        map.animateCamera(centerAndZoom, 2000, null);
    }

}
