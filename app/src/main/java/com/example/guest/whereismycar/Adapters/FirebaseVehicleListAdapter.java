package com.example.guest.whereismycar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;

import com.example.guest.whereismycar.models.Vehicle;
import com.example.guest.whereismycar.ui.VehicleDetailActivity;
import com.example.guest.whereismycar.util.ItemTouchHelperAdapter;
import com.example.guest.whereismycar.util.OnStartDragListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;

public class FirebaseVehicleListAdapter extends FirebaseRecyclerAdapter<Vehicle, FirebaseVehicleViewHolder> implements ItemTouchHelperAdapter {
    private ChildEventListener mChildEventListener;
    private ArrayList<Vehicle> mVehicles = new ArrayList<>();

    private DatabaseReference mRef;
    private OnStartDragListener mOnStartDragListener;
    private Context mContext;

    public FirebaseVehicleListAdapter(Class<Vehicle> modelClass, int modelLayout, Class<FirebaseVehicleViewHolder> viewHolderClass, Query ref, OnStartDragListener onStartDragListener, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mRef = ref.getRef();
        mOnStartDragListener = onStartDragListener;
        mContext = context;

        mChildEventListener = mRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mVehicles.add(dataSnapshot.getValue(Vehicle.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void populateViewHolder(final FirebaseVehicleViewHolder viewHolder, Vehicle model, int position) {
        viewHolder.bindVehicle(model);
        viewHolder.mVehicleImageView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mOnStartDragListener.onStartDrag(viewHolder);
                }
                return false;
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VehicleDetailActivity.class);
                intent.putExtra("position", viewHolder.getAdapterPosition());
                intent.putExtra("restaurants", Parcels.wrap(mVehicles));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mVehicles, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        mVehicles.remove(position);
        getRef(position).removeValue();
    }

    private void setIndexInFirebase() {
        for (Vehicle restaurant : mVehicles) {
            int index = mVehicles.indexOf(restaurant);
            DatabaseReference ref = getRef(index);
            restaurant.setIndex(Integer.toString(index));
            ref.setValue(restaurant);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        setIndexInFirebase();
        mRef.removeEventListener(mChildEventListener);
    }
}
