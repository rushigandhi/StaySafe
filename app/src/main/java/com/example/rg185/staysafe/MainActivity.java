package com.example.rg185.staysafe;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {


    TextView locationText;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi;
    private Double myLatitude;
    private Double myLongitude;
    private String myAddress;
    Geocoder geocoder;
    List<android.location.Address> addresses;

    Button heartAttackBtn;
    Button strokeBtn;
    Button bleedingBtn;
    Button breathingBtn;
    Button heatStrokeBtn;
    Button brokenBoneBtn;
    Button poisoningBtn;
    ViewFlipper viewFlipper;

    ArrayList<String> contactList;
    ArrayAdapter<String> adapter;
    EditText inputText;
    Button addButton;
    ListView contactLV;
    DatabaseHelper databaseHelper;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        locationText = (TextView) findViewById(R.id.textview3);

        heartAttackBtn = (Button) findViewById(R.id.heartAttackBtn);
        heartAttackBtn.setOnClickListener(this);

        strokeBtn = (Button) findViewById(R.id.strokeBtn);
        strokeBtn.setOnClickListener(this);

        bleedingBtn = (Button) findViewById(R.id.bleedingBtn);
        bleedingBtn.setOnClickListener(this);

        breathingBtn = (Button) findViewById(R.id.breathingBtn);
        breathingBtn.setOnClickListener(this);

        heatStrokeBtn = (Button) findViewById(R.id.heatstrokeBtn);
        heatStrokeBtn.setOnClickListener(this);

        brokenBoneBtn = (Button) findViewById(R.id.brokenBoneBtn);
        brokenBoneBtn.setOnClickListener(this);

        poisoningBtn = (Button) findViewById(R.id.poisoningBtn);
        poisoningBtn.setOnClickListener(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setMaxWaitTime(0);
        locationRequest.setSmallestDisplacement(0);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        geocoder = new Geocoder(this, Locale.getDefault());

        viewFlipper = (ViewFlipper) findViewById(R.id.myViewFlipper);
        databaseHelper = new DatabaseHelper(this);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.medical_nav) {
            // Handle the camera action
            viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.medical_main)));
        }
        if (id == R.id.security_nav) {
            // Handle the camera action
            viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.security_main)));
        }
        if (id == R.id.contacts_nav) {
            // Handle the camera action
            Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();

        try {

            addresses = geocoder.getFromLocation(myLatitude, myLongitude, 1);

            myAddress = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName() + ", " + addresses.get(0).getPostalCode();

        } catch (IOException e) {
            e.printStackTrace();
        }

        locationText.setText(myAddress);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(googleApiClient.isConnected()){
            requestLocationUpdates();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case  R.id.heartAttackBtn: {
                /*SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("", null, "I'm having a heart attack. I am currently at " + myAddress + ".", null, null);*/
                Toast.makeText(this, "Your trusted contacts have been contacted. Please wait.", Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.strokeBtn: {
                /*SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("", null, "I'm having a stroke. I am currently at " + myAddress + ".", null, null);*/
                Toast.makeText(this, "Your trusted contacts have been contacted. Please wait.", Toast.LENGTH_SHORT).show();
                break;
            }

            case  R.id.bleedingBtn: {
                /*SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("", null, "I'm bleeding. I am currently at " + myAddress + ".", null, null);*/
                Toast.makeText(this, "Your trusted contacts have been contacted. Please wait.", Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.breathingBtn: {
               /* SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("", null, "I have trouble breathing. I am currently at " + myAddress + ".", null, null);*/
                Toast.makeText(this, "Your trusted contacts have been contacted. Please wait.", Toast.LENGTH_SHORT).show();
                break;
            }
            case  R.id.heatstrokeBtn: {
               /* SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("", null, "I'm having a heatstroke. I am currently at " + myAddress + ".", null, null);*/
                Toast.makeText(this, "Your trusted contacts have been contacted. Please wait.", Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.brokenBoneBtn: {
                /*SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("", null, "I have a broken bone. I am currently at " + myAddress + ".", null, null);*/
                Toast.makeText(this, "Your trusted contacts have been contacted. Please wait.", Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.poisoningBtn: {
                /*SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("", null, "I'm poisoned. I am currently at " + myAddress + ".", null, null);*/
                Toast.makeText(this, "Your trusted contacts have been contacted. Please wait.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}
