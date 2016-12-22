package com.example.guest.whereismycar.ui;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.BitmapCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guest.whereismycar.R;
import com.example.guest.whereismycar.models.Vehicle;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VehicleDetailFragment extends Fragment  implements View.OnClickListener{
    @Bind(R.id.vehicleImageView) ImageView mVehicleImageView;
    @Bind(R.id.vehicleNameTextView) TextView mVehicleNameTextView;
    @Bind(R.id.vehicleDescriptionTextView) TextView mVehicleDescriptionTextView;
    @Bind(R.id.vehicleLocationTextView) TextView mVehicleLocationTextView;

    private Vehicle mVehicle;

    public static VehicleDetailFragment newInstance(Vehicle vehicle) {
        VehicleDetailFragment vehicleDetailFragment = new VehicleDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("vehicle", Parcels.wrap(vehicle));
        vehicleDetailFragment.setArguments(args);
        return vehicleDetailFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVehicle = Parcels.unwrap(getArguments().getParcelable("vehicle"));
    }

    @Override
    public void onClick(View view) {
        if(view == mVehicleLocationTextView) {
            Intent mapIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("geo:<" + mVehicle.getLatitude()
                    + ">,<" + mVehicle.getLongitude()
                    + ">?q=<" + mVehicle.getLatitude()
                    + ">,<" + mVehicle.getLongitude()
                    + ">(My Vehicle)"));
            startActivity(mapIntent);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vehicle_detail, container, false);
        ButterKnife.bind(this, view);

        try{
            Bitmap image = decodeFromFirebaseBase64(mVehicle.getVehicleImage());
            mVehicleImageView.setImageBitmap(image);
        }catch (IOException e) {
            e.printStackTrace();
        }

        mVehicleNameTextView.setText(mVehicle.getVehicleName());
        mVehicleDescriptionTextView.setText(mVehicle.getVehicleDescription());
        mVehicleLocationTextView.setText(mVehicle.getCoordinates());

        mVehicleLocationTextView.setOnClickListener(this);

        return view;
    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

}
