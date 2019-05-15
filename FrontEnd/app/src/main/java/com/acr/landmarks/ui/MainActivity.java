package com.acr.landmarks.ui;

import android.Manifest;
import android.app.ActionBar;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.acr.landmarks.R;
import com.acr.landmarks.adapters.SectionsPagerAdapter;
import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.models.Tour;
import com.acr.landmarks.services.LocationService;
import com.acr.landmarks.services.contracts.ILocationService;
import com.acr.landmarks.view_models.LandmarksViewModel;
import com.acr.landmarks.view_models.UserLocationViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import static com.acr.landmarks.Constants.ERROR_DIALOG_REQUEST;
import static com.acr.landmarks.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.acr.landmarks.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends AppCompatActivity implements LandmarkSelectedListener , TourSelectedListener{


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private static final String TAG = "Maps Activity";
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;

    private LocationCallback locationCallback;
    private ILocationService locationService;
    private UserLocationViewModel locationViewModel;

    private BottomSheetBehavior mBottomSheetBehaviour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Create the adapter that will return a fragment for each of the three primary sections of the activity.*/
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        createLocationCallback();

        createBottomSheet();

        locationService = LocationService.getInstance();
        locationViewModel = ViewModelProviders.of(this).get(UserLocationViewModel.class);

    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                locationService.setLocation(locationResult.getLastLocation());
                locationViewModel.setLocation(locationResult.getLastLocation());
            };
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapServicesAvailable()) {
            if (mLocationPermissionGranted) {
                startTrackingLocation();
            } else {
                getLocationPermission();
            }
        }
    }

    private void startTrackingLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,locationCallback,null);
    }

    private boolean mapServicesAvailable(){
        return isPlayServicesOK() && isMapsEnabled();
    }

    public boolean isPlayServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void getLocationPermission() {
        if (hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            mLocationPermissionGranted = true;
            startTrackingLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private boolean hasPermission(String aPermission){
        return ContextCompat.checkSelfPermission(this.getApplicationContext(),
                aPermission)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    onResume();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    startTrackingLocation();
                }
                else{
                    getLocationPermission();
                }
            }
        }
    }

    private void createBottomSheet() {
        View bottomSheet = findViewById(R.id.bottom_sheet_layout);
        mBottomSheetBehaviour= BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if( id == android.R.id.home){
            TabHost tabHost = findViewById(R.id.tabs);
            tabHost.setCurrentTab(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag("Map Fragment");
            mapFragment.resetTheMap();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLandmarkSelected(Landmark selectedLandmark) {
        LinearLayout layoutBottomSheet= findViewById(R.id.bottom_sheet_layout) ;
        ImageView sheetLandmarkImage =  layoutBottomSheet.findViewById(R.id.landmarkImage) ;
        TextView sheetLandmarkName =  layoutBottomSheet.findViewById(R.id.landmarkName) ;
        TextView sheetLandmarkDescription =  layoutBottomSheet.findViewById(R.id.landmarkDescription) ;
        TextView sheetLandmarkDistance =  layoutBottomSheet.findViewById(R.id.landmarkDistance) ;


        String image = selectedLandmark.getImg();
        byte[] imageData = Base64.decode(image, Base64.DEFAULT);
        Bitmap landmark = BitmapFactory.decodeByteArray(imageData,0,imageData.length);
        sheetLandmarkImage.setImageBitmap(landmark);
        sheetLandmarkName.setText(selectedLandmark.getName());
        sheetLandmarkDescription.setText(selectedLandmark.getDescription());

        String distance = ""+selectedLandmark.getDistance();
        distance += " Km";

        sheetLandmarkDistance.setText(distance);

        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onTourSelected(Tour selected) {
        generateBackButton();
        TabHost tabHost = findViewById(R.id.tabs);
        tabHost.setCurrentTab(1);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag("Map Fragment");
        mapFragment.drawTour(selected);
    }

    public void generateBackButton(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


}
