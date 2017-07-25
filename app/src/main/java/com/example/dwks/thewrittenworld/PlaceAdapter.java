package com.example.dwks.thewrittenworld;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by User on 24/07/2017.
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceItemViewHolder> {

    public final String TAG = PlaceAdapter.class.getSimpleName();
   Constants constants = Constants.getInstance();
private int mNumberOfItems;

    public PlaceAdapter() {
    }

    @Override
    public PlaceItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context  = parent.getContext();
        int layoutIdForListItem = R.layout.place_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;


        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        Log.d(TAG, view.toString());

        return new PlaceItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceItemViewHolder holder, int position) {
            String bookTitle = constants.placeObjects.get(position).getBookTitle();
        holder.placeTitle.setText(bookTitle);

    }

    @Override
    public int getItemCount() {
        return constants.placeObjects.size();
    }

    //View holder for the Place Item
    public class PlaceItemViewHolder extends RecyclerView.ViewHolder {
       public final TextView placeTitle;

        public PlaceItemViewHolder(View itemView){
            super(itemView);

            placeTitle = (TextView) itemView.findViewById(R.id.place_view_item);
        }

    }
}
