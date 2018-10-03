package com.jukulex.juz;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MapsActivity extends SupportMapFragment implements OnMapReadyCallback, OnMapLongClickListener {

    private static final String LOGTAG = "juzapp - MapsActivity";

    private Context mActivity;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CollectionReference mMarkerColRef = db.collection("Markers");
    private UserProperties mCurrentUserProperties;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mActivity = getActivity();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
    }

    @Override
    public void onResume() {
        super.onResume();

        getUserProperties();

        if (mMap == null) {
            getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);
        else
            Log.d(LOGTAG, "Location permission not granted");

        addBuiltinMarkers();

        fetchMarkersFromDb();

//        getLastKnownLocation();
    }

    @Override
    public void onMapLongClick(final LatLng point) {
        if (mCurrentUserProperties == null || !mCurrentUserProperties.isPostMapMarkersAllowed()) {
            Toast.makeText(mActivity, "Du darfst keine Marker posten.", Toast.LENGTH_LONG).show();
            return;
        }

        // create an alert dialog to let the user enter a title and an optional description
        // by creating two EditTexts which are added to a LinearLayout
        AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);

        final LinearLayout ll = new LinearLayout(mActivity);
        ll.setOrientation(LinearLayout.VERTICAL);

        final EditText inputTitle = new EditText(mActivity);
        inputTitle.setHint("Titel");
        final EditText inputDescr = new EditText(mActivity);
        inputDescr.setHint("Beschreibung");

        ll.addView(inputTitle);
        ll.addView(inputDescr);
        alert.setTitle("Bitte Titel und Beschreibung eingeben");
        alert.setView(ll);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // title is mandatory
                String title = inputTitle.getText().toString();
                if (title.length() == 0)
                    return;

                String snippet = inputDescr.getText().toString();
                if (snippet.length() == 0)
                    snippet = null;

                // place the marker onto the map
                mMap.addMarker(new MarkerOptions().position(point).title(title).snippet(snippet));

                // save the new marker to the database
                MapMarker mm = new MapMarker(new GeoPoint(point.latitude, point.longitude), title, snippet, mAuth.getUid());
                mMarkerColRef.add(mm);

            }
        });

        alert.setNegativeButton("Abbrechen", null);

        alert.show();

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
        mMarkerColRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docSnapshots = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot ds : docSnapshots) {
                    try {
                        MapMarker mm = ds.toObject(MapMarker.class);
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(mm.getLocation().getLatitude(), mm.getLocation().getLongitude()))
                                .title(mm.getTitle())
                                .snippet(mm.getSnippet())
                        );
                    } catch (NullPointerException e) {}

                }
            }

        });
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    Log.d(LOGTAG, "onComplete: latitude: " + geoPoint.getLatitude());
                    Log.d(LOGTAG, "onComplete: longitude: " + geoPoint.getLongitude());
                }
            }
        });

    }

    private void getUserProperties() {
        if (mAuth.getUid() == null) {
            mCurrentUserProperties = new UserProperties();
        } else {
            String userPropDocPath = "UserProperties/" + mAuth.getUid();

            DocumentReference userPropDocRef = db.document(userPropDocPath);
            userPropDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        mCurrentUserProperties = documentSnapshot.toObject(UserProperties.class);
                    }
                }
            });
        }
    }

}
