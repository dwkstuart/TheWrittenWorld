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
public class PlaceDetailRecyclerViewAdapter extends RecyclerView.Adapter<PlaceDetailRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceObject> mValues;
    private final OnListFragmentInteractionListener mListener;

    public PlaceDetailRecyclerViewAdapter(List<PlaceObject> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_placedetail, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {



        holder.mItem = mValues.get(holder.getAdapterPosition());
        holder.mAuthorName.setText(mValues.get(holder.getAdapterPosition()).getAuthorName());
        holder.mContentView.setText(mValues.get(holder.getAdapterPosition()).getLocation());
        holder.visited.setChecked(mValues.get(holder.getAdapterPosition()).isVisited());
        holder.quote.setText(mValues.get(holder.getAdapterPosition()).getAssociatedQuote());
        holder.title.setText(mValues.get(holder.getAdapterPosition()).getBookTitle());


        //set respond to check box
        holder.visited.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (holder.visited.isChecked()){
                                                mValues.get(holder.getAdapterPosition()).setVisited(true);
                                            }
                                            else if(!holder.visited.isChecked())
                                                mValues.get(holder.getAdapterPosition()).setVisited(false);

                                        }

                                    });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                    PlaceObject object = holder.mItem;

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
        public final TextView quote;
        public final TextView title;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAuthorName = view.findViewById(R.id.name);
            mContentView = view.findViewById(R.id.content);
            visited = view.findViewById(R.id.visitedCheckList);
            quote = view.findViewById(R.id.list_quote);
            title = view.findViewById(R.id.booktitle);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
