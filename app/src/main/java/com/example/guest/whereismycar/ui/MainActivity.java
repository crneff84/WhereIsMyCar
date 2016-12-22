package com.example.guest.whereismycar.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guest.whereismycar.Constants;
import com.example.guest.whereismycar.Manifest;
import com.example.guest.whereismycar.R;
import com.example.guest.whereismycar.models.Vehicle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    @Bind(R.id.saveVehicleButton) Button mSaveVehicleButton;
    @Bind(R.id.vehicleLocationButton) Button mVehicleLocationButton;
    @Bind(R.id.vehicleNameEditText) EditText mVehicleNameEditText;
    @Bind(R.id.vehicleDescriptionEditText) EditText mVehicleDescriptionEditText;
    @Bind(R.id.imageTextView) TextView mImageTextView;
    @Bind(R.id.vehicleNameTextView) TextView mVehicleNameTextView;
    @Bind(R.id.vehicleDescriptionTextView) TextView mVehicleDescriptionTextView;
    @Bind(R.id.cameraIcon) ImageView mCameraIcon;
    @Bind(R.id.locationTextView) TextView mLocationTextView;
    @Bind(R.id.updateLocationButton) Button mUpdateLocationButton;

    private static final String TAG = MainActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    static final Integer LOCATION = 1;
    private FusedLocationProviderApi mFusedLocationProviderApi;
    private String mLatitude;
    private String mLongitude;
    private boolean locationUpdated;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private Vehicle mVehicle;
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private String mVehicleImage = "";
    private String mCoordinates = "";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Typeface captureFont = Typeface.createFromAsset(getAssets(), "fonts/Capture_it.ttf");
        mSaveVehicleButton.setTypeface(captureFont);
        mVehicleLocationButton.setTypeface(captureFont);
        mUpdateLocationButton.setTypeface(captureFont);
        mVehicleNameEditText.setTypeface(captureFont);
        mVehicleDescriptionEditText.setTypeface(captureFont);
        mImageTextView.setTypeface(captureFont);
        mVehicleNameTextView.setTypeface(captureFont);
        mVehicleDescriptionTextView.setTypeface(captureFont);

        mUpdateLocationButton.setOnClickListener(this);
        mSaveVehicleButton.setOnClickListener(this);
        mVehicleLocationButton.setOnClickListener(this);
        mCameraIcon.setOnClickListener(this);

        locationUpdated = false;

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    getSupportActionBar().setTitle("Welcome, " + user.getDisplayName() + "!");
                } else {

                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        if(view == mCameraIcon) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

        if(view == mUpdateLocationButton) {
            findLocation();
        }

        if(view == mSaveVehicleButton) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();
            String vehicleName = mVehicleNameEditText.getText().toString();
            String vehicleDescription = mVehicleDescriptionEditText.getText().toString();
            String vehicleImage = mVehicleImage;
            String coordinates = mCoordinates;

            mVehicle = new Vehicle(vehicleName, vehicleDescription, vehicleImage, coordinates);

            DatabaseReference vehicleRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constants.FIREBASE_CHILD_VEHICLES)
                    .child(uid);

            DatabaseReference pushRef = vehicleRef.push();
            String pushId = pushRef.getKey();
            mVehicle.setPushId(pushId);
            pushRef.setValue(mVehicle);

            Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
        }

        if(view == mVehicleLocationButton) {
            Intent intent = new Intent(MainActivity.this, VehiclesActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        mAuth.addAuthStateListener(mAuthListener);
        if(mGoogleApiClient!=null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

//    @Override
//    protected void OnResume() {
//        super.onResume();
//        checkPlayServices();
//    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == MainActivity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            encodeBitmap(imageBitmap);
        }
    }

    public void encodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        mVehicleImage = imageEncoded;
    }

    public void findLocation(){
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,LOCATION);
        } else {
            mLocationRequest = mLocationRequest.create();
            mLocationRequest.setPriority(mLocationRequest.PRIORITY_HIGH_ACCURACY);
            mFusedLocationProviderApi = LocationServices.FusedLocationApi;
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        }
        if (locationUpdated == false){
            if (mSharedPreferences.getString(Constants.PREFERENCES_LATITUDE_KEY, null) !=null && mSharedPreferences.getString(Constants.PREFERENCES_LONGITUDE_KEY, null)!= null){
//                TODO this oughtta be a callback!
                mLatitude = mSharedPreferences.getString(Constants.PREFERENCES_LATITUDE_KEY, null);
                mLongitude = mSharedPreferences.getString(Constants.PREFERENCES_LONGITUDE_KEY, null);
                mLocationTextView.setText("Latitude: " + mLatitude + "Longitude: " + mLongitude);
            }
        }
    }

    public void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(MainActivity.this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
        findLocation();
    }

//    public void onLocationChanged(Location location) {
//        mLatitude = String.valueOf(location.getLatitude());
//        mLongitude = String.valueOf(location.getLongitude());
//        mEditor.putString(Constants.PREFERENCES_LATITUDE_KEY, mLatitude).apply();
//        mEditor.putString(Constants.PREFERENCES_LONGITUDE_KEY, mLongitude).apply();
//        mLocationTextView.setText("Latitude: " + mLatitude + "Longitude: " + mLongitude);
//        locationUpdated = true;
//    }


    @Override
    public void onConnected(Bundle connectionHint) {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
    }

    @Override
    public void onConnectionSuspended(int i){

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){

    }
}
