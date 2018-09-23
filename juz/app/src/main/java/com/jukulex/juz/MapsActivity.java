package com.jukulex.juz;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MapsActivity extends SupportMapFragment implements OnMapReadyCallback, OnMapLongClickListener {

    private GoogleMap mMap;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//        .setTimestampsInSnapshotsEnabled(true)
//        .build();


    private CollectionReference mCollectionRef = firestore.collection("Markers");

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

//        firestore.setFirestoreSettings(settings);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMap == null) {
            getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
            //mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapLongClickListener(this);

        addBuiltinMarkers();

        fetchMarkersFromDb();
    }

    @Override
    public void onMapLongClick(LatLng point) {
        LatLng marker = new LatLng(point.latitude, point.longitude);
        mMap.addMarker(new MarkerOptions().position(marker).title("new Marker"));
    }

    private void addBuiltinMarkers() {

        // Juz Silixen
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.138081, 9.060906))
                .title("Mehrgenerationenhaus")
                .snippet("Dietrich-Bonhoeffer-Str. 2, 32699 Extertal")
        );

        // Juz Bösingfeld
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.067692, 9.130269))
                .title("Cinema 55")
                .snippet("Mittelstr. 55, 32699 Extertal")
        );

        // Juz Almena. Center camera around this marker
        LatLng haeuschen = new LatLng(52.105718, 9.082175);
        mMap.addMarker(new MarkerOptions()
                .position(haeuschen)
                .title("Häuschen")
                .snippet("Fütiger Str. 34, 32699 Extertal"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(haeuschen, 15.0f));

    }

    private void fetchMarkersFromDb() {
        mCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docSnapshots = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot ds : docSnapshots) {
                    GeoPoint gp = ds.getGeoPoint("location");
                    if (gp == null) {
                        return;
                    }
                    String name = ds.getString("name");
                    if (name == null) {
                        return;
                    }

                    //addMarkerToMap(new LatLng(gp.getLatitude(), gp.getLongitude()), name);
                    mMap.addMarker(new MarkerOptions().position(new LatLng(gp.getLatitude(), gp.getLongitude())).title(name));
                }
            }

        });
    }
}
