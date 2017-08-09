package com.example.dwks.thewrittenworld;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.dwks.thewrittenworld.PlaceDetailFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceObject} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyPlaceDetailRecyclerViewAdapter extends RecyclerView.Adapter<MyPlaceDetailRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceObject> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;

    public MyPlaceDetailRecyclerViewAdapter(List<PlaceObject> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context =parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_placedetail, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mItem = mValues.get(position);
        holder.mAuthorName.setText(mValues.get(position).getAuthorFirstName() + " " +mValues.get(position).getAuthorSecondName());
        holder.mContentView.setText(mValues.get(position).getLocation());
        holder.visited.setChecked(mValues.get(position).isVisited());

        //set respond to check box
        holder.visited.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (holder.visited.isChecked()){
                                                mValues.get(position).setVisited(true);
                                            }
                                            else if(holder.visited.isChecked()== false)
                                                mValues.get(position).setVisited(false);

                                        }

                                    });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                    PlaceObject object = holder.mItem;
//                    String id = object.getDb_key();
//
//                    Intent placeDetails = new Intent(context,PlaceDetailScreen.class);
//                    placeDetails.putExtra("ID", id);
//                    context.startActivity(placeDetails);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAuthorName;
        public final TextView mContentView;
        public CheckBox visited;
        public PlaceObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAuthorName = (TextView) view.findViewById(R.id.name);
            mContentView = (TextView) view.findViewById(R.id.content);
            visited = (CheckBox) view.findViewById(R.id.visitedCheckList);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
