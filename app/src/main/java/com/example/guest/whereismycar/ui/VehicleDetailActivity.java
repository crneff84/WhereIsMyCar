package com.example.guest.whereismycar.ui;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.guest.whereismycar.Adapters.VehiclePagerAdapter;
import com.example.guest.whereismycar.R;
import com.example.guest.whereismycar.models.Vehicle;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VehicleDetailActivity extends AppCompatActivity {
    @Bind(R.id.viewPager) ViewPager mViewPager;
    private VehiclePagerAdapter adapterViewPager;
    ArrayList<Vehicle> mVehicles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_detail);
        ButterKnife.bind(this);

        mVehicles = Parcels.unwrap(getIntent().getParcelableExtra("vehicles"));
        int startingPosition = getIntent().getIntExtra("position", 0);

        adapterViewPager = new VehiclePagerAdapter(getSupportFragmentManager(), mVehicles);
        mViewPager.setAdapter(adapterViewPager);
        mViewPager.setCurrentItem(startingPosition);
    }
}
