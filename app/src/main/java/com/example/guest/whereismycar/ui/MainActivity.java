package com.example.guest.whereismycar.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;


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

        if (checkPlayServices()) {
            buildGoogleApiClient();
        }

    }

    @Override
    public void onClick(View view) {
        if(view == mCameraIcon) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

        if(view == mUpdateLocationButton) {
            displayLocation();
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
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        if(mGoogleApiClient!=null) {
            mGoogleApiClient.connect();
        }
    }

//    @Override
//    protected void OnResume() {
//        super.onResume();
//        checkPlayServices();
//    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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

    public void displayLocation() {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.d(TAG, String.valueOf(mLastLocation));

            if(mLastLocation != null) {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();

                mLocationTextView.setText(latitude + "," + longitude);
            } else {
                mLocationTextView.setText("Unable to Find Location");
            }
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), "Location Services Must Be Activated in Settings", Toast.LENGTH_LONG).show();
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS) {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This Device is not Supported", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        //displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }
}
