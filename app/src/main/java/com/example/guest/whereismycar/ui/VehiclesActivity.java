package com.example.guest.whereismycar.ui;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.guest.whereismycar.Adapters.FirebaseVehicleViewHolder;
import com.example.guest.whereismycar.Adapters.VehicleListAdapter;
import com.example.guest.whereismycar.Constants;
import com.example.guest.whereismycar.R;
import com.example.guest.whereismycar.models.Vehicle;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VehiclesActivity extends AppCompatActivity {
    private DatabaseReference mVehicleReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicles);

        ButterKnife.bind(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        mVehicleReference = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_CHILD_VEHICLES)
                .child(uid);

        setUpFirebaseAdapter();
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Vehicle, FirebaseVehicleViewHolder>
                (Vehicle.class, R.layout.vehicle_list_item, FirebaseVehicleViewHolder.class, mVehicleReference) {
            @Override
            protected void populateViewHolder(FirebaseVehicleViewHolder viewHolder, Vehicle model, int position) {
                viewHolder.bindVehicle(model);
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseAdapter.cleanup();
    }
}
