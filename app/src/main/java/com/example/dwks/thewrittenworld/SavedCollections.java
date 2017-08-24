package com.example.dwks.thewrittenworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.TreeSet;

public class SavedCollections extends AppCompatActivity implements SavedFiles.OnListFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name = String.valueOf(getCallingActivity());
        Log.d("Calling activity", name);
        setContentView(R.layout.activity_saved_collections);
    }

    @Override
    public void onListFragmentInteraction(SavedCollection item) {
        TreeSet<PlaceObject> savedSet = (TreeSet<PlaceObject>) item.getSelection();
       // Constants.placeObjects = (TreeSet) savedSet;
        Intent currentList = new Intent(this,PlaceObjectList.class);
        ArrayList<PlaceObject> loadedPlaceList = new ArrayList<>();
        loadedPlaceList.addAll(savedSet);
        currentList.putParcelableArrayListExtra("LIST",loadedPlaceList);
        currentList.putExtra("FILE_NAME",item.getListName());
        startActivity(currentList);
//        Log.d("Size of set", String.valueOf(savedSet.size()));

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
