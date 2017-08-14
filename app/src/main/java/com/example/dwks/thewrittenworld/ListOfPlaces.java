package com.example.dwks.thewrittenworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ListOfPlaces extends AppCompatActivity implements PlaceDetailFragment.OnListFragmentInteractionListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_places);

//        if(findViewById(R.id.placeList) !=null){
//
//            PlaceDetailFragment placeDetailFragment = new PlaceDetailFragment();
//
//            getSupportFragmentManager().beginTransaction().add(R.id.placeList,placeDetailFragment).commit();
//        }

    }

    @Override
    public void onListFragmentInteraction(PlaceObject item) {
        Toast.makeText(this, "Item is visited? " + String.valueOf(item.isVisited()), Toast.LENGTH_LONG).show();
        String id = item.getDb_key();

        Constants.places.put(id,item);
        Log.d("List from constants", Constants.places.get(id).toString());
        Intent placeDetails = new Intent(this,PlaceDetailScreen.class);
        placeDetails.putExtra("ID", id);
        Log.d("List from passed", item.toString());
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
