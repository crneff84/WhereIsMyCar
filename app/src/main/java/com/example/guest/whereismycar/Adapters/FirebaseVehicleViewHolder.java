package com.example.guest.whereismycar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guest.whereismycar.Constants;
import com.example.guest.whereismycar.R;
import com.example.guest.whereismycar.models.Vehicle;
import com.example.guest.whereismycar.ui.VehicleDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by Guest on 12/22/16.
 */
public class FirebaseVehicleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final int MAX_WIDTH = 200;
    private static final int MAX_HEIGHT = 200;

    View mView;
    Context mContext;

    public FirebaseVehicleViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindVehicle(Vehicle vehicle) {
        ImageView vehicleImageView = (ImageView) mView.findViewById(R.id.vehicleImageView);
        TextView vehicleNameTextView = (TextView) mView.findViewById(R.id.vehicleNameTextView);
        TextView vehicleDescriptionTextView = (TextView) mView.findViewById(R.id.vehicleDescriptionTextView);
        TextView vehicleLocationTextView = (TextView) mView.findViewById(R.id.vehicleLocationTextView);

//        Picasso.with(mContext)
//                .load(vehicle.getImageUrl())
//                .resize(MAX_WIDTH, MAX_HEIGHT)
//                .centerCrop()
//                .into(vehicleImageView);

        vehicleNameTextView.setText(vehicle.getVehicleName());
        vehicleDescriptionTextView.setText(vehicle.getVehicleDescription());
        vehicleLocationTextView.setText(vehicle.getCoordinates());
    }

    public void onClick(View view) {
        final ArrayList<Vehicle> vehicles = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_VEHICLES);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    vehicles.add(snapshot.getValue(Vehicle.class));
                }

                int itemPosition = getLayoutPosition();

                Intent intent = new Intent(mContext, VehicleDetailActivity.class);
                intent.putExtra("position", itemPosition + "");
                intent.putExtra("vehicles", Parcels.wrap(vehicles));

                mContext.startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
