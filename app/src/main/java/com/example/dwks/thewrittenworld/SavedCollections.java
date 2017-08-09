package com.example.dwks.thewrittenworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SavedCollections extends AppCompatActivity implements SavedFiles.OnListFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_collections);
    }

    @Override
    public void onListFragmentInteraction(SavedCollection item) {

    }
}
