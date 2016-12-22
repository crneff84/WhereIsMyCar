package com.example.guest.whereismycar.Adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.guest.whereismycar.models.Vehicle;
import com.example.guest.whereismycar.ui.VehicleDetailFragment;

import java.util.ArrayList;


public class VehiclePagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Vehicle> mVehicles;

    public VehiclePagerAdapter(FragmentManager fm, ArrayList<Vehicle> vehicles) {
        super(fm);
        mVehicles = vehicles;
    }

    @Override
    public Fragment getItem(int position) {
        return VehicleDetailFragment.newInstance(mVehicles.get(position));
    }

    @Override
    public int getCount() {
        return mVehicles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mVehicles.get(position).getVehicleName();
    }
}
