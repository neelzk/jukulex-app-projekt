package com.jukulex.juz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOGTAG = "juzapp - MainActivity";
    private static final int USER_AUTH_REQUEST_CODE = 1;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CollectionReference mUserCollectionRef = FirebaseFirestore.getInstance().collection("UserProperties");
    private TextView mTvNavheaderTitle;
    private TextView mTvNavheaderSubtitle;
    private MenuItem mUserSection;
    private boolean mGoogleServicesOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleServicesOK = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this) == ConnectionResult.SUCCESS;
        Log.d(LOGTAG, "services OK? " + mGoogleServicesOK);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        mTvNavheaderTitle = navigationView.getHeaderView(0).findViewById(R.id.tv_navheader_title);
        mTvNavheaderSubtitle = navigationView.getHeaderView(0).findViewById(R.id.tv_navheader_subtitle);
        mUserSection = navigationView.getMenu().findItem(R.id.menu_user);

        updateViewsOnLoginChange();

        if (!mGoogleServicesOK)
            mTvNavheaderSubtitle.setText("");

        replaceFragment(new HomeFragment(), "Start", false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                replaceFragment(new HomeFragment(), "Start",  false);
                break;
            case R.id.nav_events:
                if (mGoogleServicesOK)
                    replaceFragment(new EventsFragment(), "Termine", false);
                else
                     replaceFragment(new EventsFragmentNoGoogle(), "Termine", false);
                break;
            case R.id.nav_impressum:
                replaceFragment(new ImpressumFragment(), "Impressum", false);
                break;
            case R.id.nav_maps:
                replaceFragment(new MapsActivity(), "Maps", false);
                break;
            case R.id.nav_feedback:
                replaceFragment(new FeedbackFragment(), "Feedback", false);
                break;
            case R.id.nav_oepnv:
                replaceFragment(new OepnvFragment(), "ÖPNV", false);
                break;
            case R.id.nav_logout:
                logoutUser();
                return false;
            case R.id.nav_deleteaccount:
                deleteAccount();
                return false;

            default:
                return false;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onNavHeaderClicked(View v) {
        if (mGoogleServicesOK) {
            if (mAuth.getCurrentUser() == null) {
                Log.d(LOGTAG, "current user == null");
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.FacebookBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build());

                startActivityForResult(AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        USER_AUTH_REQUEST_CODE);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == USER_AUTH_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                Log.d(LOGTAG, "Successfully signed in");
                updateViewsOnLoginChange();

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.

                if (response != null) {
                    Toast.makeText(this, "Login fehlgeschlagen: " + response.getError().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void logoutUser() {
        Log.d(LOGTAG, "logoutUser()");

        if (mAuth.getCurrentUser() != null) {
            final Context ctx = this;
            AlertDialog.Builder adb = new AlertDialog.Builder(ctx);
            adb.setMessage("Ausloggen?")
                    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AuthUI.getInstance().signOut(ctx).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getBaseContext(), "Ausgeloggt", Toast.LENGTH_LONG).show();
                                        updateViewsOnLoginChange();
                                    } else {
                                        Toast.makeText(getBaseContext(), "Ausloggen fehlgeschlagen", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    })
                    .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
    }

    private void deleteAccount() {
        Log.d(LOGTAG, "deleteAccount()");

        if (mAuth.getCurrentUser() != null) {
            final Context ctx = this;
            final String userId = mAuth.getUid();

            AlertDialog.Builder adb = new AlertDialog.Builder(ctx);
            adb.setMessage("Account löschen?")
                    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AuthUI.getInstance().delete(ctx).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getBaseContext(), "Account gelöscht", Toast.LENGTH_LONG).show();
                                        updateViewsOnLoginChange();

                                    } else {
                                        Toast.makeText(getBaseContext(), "Account löschen fehlgeschlagen: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    })
                    .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
    }

    private void updateViewsOnLoginChange() {
        // TODO: update possible views of the currently active fragment
//        Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        FirebaseUser currUser = mAuth.getCurrentUser();
        if (currUser != null) {
            Toast.makeText(this, "Eingeloggt als " + currUser.getDisplayName(), Toast.LENGTH_LONG).show();
            mTvNavheaderTitle.setText(currUser.getDisplayName());
            mTvNavheaderSubtitle.setText(currUser.getEmail());
            mUserSection.setVisible(true);
        } else {
            mTvNavheaderTitle.setText(R.string.nav_header_title);
            mTvNavheaderSubtitle.setText(R.string.nav_header_subtitle);
            mUserSection.setVisible(false);
        }
    }

    private void replaceFragment(Fragment fragment, String tag, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        // only the mapsframent will be added to the stack, so we're always popping here
        // to make sure at most one fragment remains on the stack.
        // ensures having an empty stack, even if the user switches from maps to another fragment by menu
//        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);

//        if (addToBackStack) {
//            transaction.addToBackStack(null);
//        }

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("JuKulEx App - " + tag);

        transaction.commit();
    }

}
