package com.example.dwks.thewrittenworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ListOfPlaces extends AppCompatActivity implements PlaceDetailFragment.OnListFragmentInteractionListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_places);



    }

    @Override
    public void onListFragmentInteraction(PlaceObject item) {
        Toast.makeText(this, String.valueOf(item.getLongitude()), Toast.LENGTH_LONG).show();
        String id = item.getDb_key();

        Intent placeDetails = new Intent(this,PlaceDetailScreen.class);
        placeDetails.putExtra("ID", id);
        this.startActivity(placeDetails);

    }

    private ToolBarMenuHandler toolBarMenuHandler = new ToolBarMenuHandler(this);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return toolBarMenuHandler.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return toolBarMenuHandler.onOptionsItemSelected(item);
    }

}
