package com.jukulex.juz;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;

public class MapsActivity extends SupportMapFragment implements OnMapReadyCallback, OnMapLongClickListener {

    private GoogleMap mMap;

    @Override
    public void onResume() {
        super.onResume();

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {

        if (mMap == null) {
            getMapAsync(this);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        // Add Häuschen Almena marker and move the camera
        LatLng haeuschen = new LatLng(52.1058, 9.0823);
        mMap.addMarker(new MarkerOptions().position(haeuschen).title("Häuschen in Almena"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(haeuschen,15.0f));
    }

    @Override
    public void onMapLongClick(LatLng point) {
        LatLng marker = new LatLng(point.latitude, point.longitude);
        mMap.addMarker(new MarkerOptions().position(marker).title("new Marker"));
    }
}
