package com.example.guest.whereismycar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by Guest on 12/22/16.
 */
public class FirebaseVehicleViewHolder extends RecyclerView.ViewHolder {
    private static final int MAX_WIDTH = 200;
    private static final int MAX_HEIGHT = 200;

    View mView;
    Context mContext;
    public ImageView mVehicleImageView;

    public FirebaseVehicleViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
//        itemView.setOnClickListener(this);
    }

    public void bindVehicle(Vehicle vehicle) {
        mVehicleImageView = (ImageView) mView.findViewById(R.id.vehicleImageView);
        TextView vehicleNameTextView = (TextView) mView.findViewById(R.id.vehicleNameTextView);
        TextView vehicleDescriptionTextView = (TextView) mView.findViewById(R.id.vehicleDescriptionTextView);
        TextView vehicleLocationTextView = (TextView) mView.findViewById(R.id.vehicleLocationTextView);

        try{
            Bitmap imageBitmap = decodeFromFirebaseBase64(vehicle.getVehicleImage());
            mVehicleImageView.setImageBitmap(imageBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        vehicleNameTextView.setText(vehicle.getVehicleName());
        vehicleDescriptionTextView.setText(vehicle.getVehicleDescription());
    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

//    public void onClick(View view) {
//        final ArrayList<Vehicle> vehicles = new ArrayList<>();
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_VEHICLES);
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
//                    vehicles.add(snapshot.getValue(Vehicle.class));
//                }
//
//                int itemPosition = getLayoutPosition();
//
//                Intent intent = new Intent(mContext, VehicleDetailActivity.class);
//                intent.putExtra("position", itemPosition);
//                intent.putExtra("vehicles", Parcels.wrap(vehicles));
//
//                mContext.startActivity(intent);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
}
