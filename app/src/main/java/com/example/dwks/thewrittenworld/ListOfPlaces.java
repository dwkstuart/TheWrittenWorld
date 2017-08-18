package com.example.dwks.thewrittenworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ListOfPlaces extends AppCompatActivity implements PlaceDetailFragment.OnListFragmentInteractionListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_places);

    }

    @Override
    public void onListFragmentInteraction(PlaceObject item) {
        Intent placeDetails = new Intent(this,PlaceDetailScreen.class);

        placeDetails.putExtra("Place", item);
        this.startActivity(placeDetails);

    }

    private ToolBarMenuHandler toolBarMenuHandler = new ToolBarMenuHandler(this);

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        return toolBarMenuHandler.onPrepareOptionsMenu(menu);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return toolBarMenuHandler.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return toolBarMenuHandler.onOptionsItemSelected(item);
    }

}
