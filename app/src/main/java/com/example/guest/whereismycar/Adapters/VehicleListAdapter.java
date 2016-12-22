package com.example.guest.whereismycar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guest.whereismycar.R;
import com.example.guest.whereismycar.models.Vehicle;
import com.example.guest.whereismycar.ui.VehicleDetailActivity;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.VehicleViewHolder> {
    private static final int MAX_WIDTH = 200;
    private static final int MAX_HEIGHT = 200;

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

    public class VehicleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.vehicleNameTextView) TextView mVehicleNameTextView;
        @Bind(R.id.vehicleDescriptionTextView) TextView mVehicleDescriptionTextView;
        @Bind(R.id.vehicleLocationTextView) TextView mVehicleLocationTextView;
        private Context context;

        public VehicleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int itemPosition = getLayoutPosition();

            Intent intent = new Intent(mContext, VehicleDetailActivity.class);
            intent.putExtra("position", itemPosition);
            intent.putExtra("vehicles", Parcels.wrap(mVehicles));
            mContext.startActivity(intent);
        }

        public void bindVehicle(Vehicle vehicle) {
            mVehicleNameTextView.setText(vehicle.getVehicleName());
            mVehicleDescriptionTextView.setText(vehicle.getVehicleDescription());
            mVehicleLocationTextView.setText(vehicle.getCoordinates());
        }
    }
}
