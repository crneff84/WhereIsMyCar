package com.example.guest.whereismycar.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.example.guest.whereismycar.Adapters.VehicleListAdapter;
import com.example.guest.whereismycar.R;
import com.example.guest.whereismycar.models.Vehicle;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VehiclesActivity extends AppCompatActivity {
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    private VehicleListAdapter mAdapter;

    public ArrayList<Vehicle> mVehicles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicles);
        ButterKnife.bind(this);

    }
}
