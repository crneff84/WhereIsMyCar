package com.example.guest.whereismycar.util;

/**
 * Created by Guest on 12/22/16.
 */
public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
