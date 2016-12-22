package com.example.guest.whereismycar.ui;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guest.whereismycar.R;
import com.example.guest.whereismycar.models.Vehicle;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class VehicleDetailFragment extends Fragment {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vehicle_detail, container, false);
        ButterKnife.bind(this, view);

        mVehicleNameTextView.setText(mVehicle.getVehicleName());
        mVehicleDescriptionTextView.setText(mVehicle.getVehicleDescription());
        mVehicleLocationTextView.setText(mVehicle.getCoordinates());

        return view;
    }

}
