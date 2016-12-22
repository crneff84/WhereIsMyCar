package com.example.guest.whereismycar.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guest.whereismycar.R;
import com.example.guest.whereismycar.models.Vehicle;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Guest on 12/22/16.
 */
public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.VehicleViewHolder> {
    private ArrayList<Vehicle> mVehicles = new ArrayList<>();
    private Context mContext;

    public VehicleListAdapter(Context context, ArrayList<Vehicle> vehicles) {
        mContext = context;
        mVehicles = vehicles;
    }

    @Override
    public VehicleListAdapter.VehicleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_list_item, parent, false);
        VehicleViewHolder viewHolder = new VehicleViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VehicleListAdapter.VehicleViewHolder holder, int position) {
        holder.bindVehicle(mVehicles.get(position));
    }

    @Override
    public int getItemCount() {
        return mVehicles.size();
    }

    public class VehicleViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.vehicleNameTextView) TextView mVehicleNameTextView;
        @Bind(R.id.vehicleDescriptionTextView) TextView mVehicleDescriptionTextView;
        @Bind(R.id.vehicleLocationTextView) TextView mVehicleLocationTextView;
        private Context context;

        public VehicleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindVehicle(Vehicle vehicle) {
            mVehicleNameTextView.setText(vehicle.getVehicleName());
            mVehicleDescriptionTextView.setText(vehicle.getVehicleDescription());
            mVehicleLocationTextView.setText(vehicle.getCoordinates());
        }
    }
}
