package com.example.dwks.thewrittenworld;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dwks.thewrittenworld.SavedFiles.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SavedCollection} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class SavedCollectionRecyclerViewAdapter extends RecyclerView.Adapter<SavedCollectionRecyclerViewAdapter.ViewHolder> {

    private final List<SavedCollection> mValues;
    private final OnListFragmentInteractionListener mListener;

    public SavedCollectionRecyclerViewAdapter(List<SavedCollection> items, OnListFragmentInteractionListener listener) {

        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_savedcollection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText("Visited " +  mValues.get(position).getNumberVisited() +
                " /" + mValues.get(position).getNumPlaces());
        holder.mContentView.setText("Collection Name: " +
                "\n" + mValues.get(position).getListName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
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
        public final TextView mIdView;
        public final TextView mContentView;
        public SavedCollection mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.id);
            mContentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
