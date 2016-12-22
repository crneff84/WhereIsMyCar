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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
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

    private Vehicle mVehicle;
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private String mVehicleImage = "";
    private String mCoordinates = "";

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLocation;
    private String mLocationProvider;
    private Location mLastLocation;

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

        mLocationProvider = LocationManager.NETWORK_PROVIDER;
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

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
            mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mLocation = location;
                    mCoordinates = ("Lat:" + location.getLatitude() + ", Long:" + location.getLongitude());
                    mLocationTextView.setText(mCoordinates);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {}
            };

            if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,mLocationListener);
                mLastLocation = mLocationManager.getLastKnownLocation(mLocationProvider);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }

        if(view == mSaveVehicleButton) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();
            String vehicleName = mVehicleNameEditText.getText().toString();
            String vehicleDescription = mVehicleDescriptionEditText.getText().toString();
            String vehicleImage = mVehicleImage;
            String coordinates = mCoordinates;

            boolean validName = isValidName(vehicleName);
            boolean validDescription = isValidDescription(vehicleDescription);
            boolean validImage = isValidImage(vehicleImage);
            boolean validLocation = isValidLocation(coordinates);


            if (!validName || !validDescription || !validImage || !validLocation) return;

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case 0: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)  {
                    if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
                        mLastLocation = mLocationManager.getLastKnownLocation(mLocationProvider);
                    }
                } else {

                }
                break;
            }
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
    }

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

    private boolean isValidName(String vehicleName) {
        if(vehicleName.equals("")) {
            mVehicleNameEditText.setError("Enter a Vehicle Name");
            return false;
        }
        return true;
    }

    private boolean isValidDescription(String vehicleDescription) {
        if(vehicleDescription.equals("")) {
            mVehicleDescriptionEditText.setError("Enter a Vehicle Description");
            return false;
        }
        return true;
    }

    private boolean isValidImage(String vehicleImage) {
        if(vehicleImage.equals("")) {
            Toast.makeText(MainActivity.this, "Take a Picture of Your Location", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isValidLocation(String coordinates) {
        if(coordinates.equals("")) {
            Toast.makeText(MainActivity.this, "Update Your Location", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


}
