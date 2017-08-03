package com.example.dwks.thewrittenworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

public class ListOfPlaces extends AppCompatActivity {

    PlaceAdapter placeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_places);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_places);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayout.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        placeAdapter = new PlaceAdapter();

        recyclerView.setAdapter(placeAdapter);

    }
}
