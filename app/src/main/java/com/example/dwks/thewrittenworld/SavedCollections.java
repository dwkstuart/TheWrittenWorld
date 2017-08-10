package com.example.dwks.thewrittenworld;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
        Constants.placeObjects = (TreeSet) savedSet;
        Log.d("Size of set", String.valueOf(savedSet.size()));
        Toast.makeText(this,"Loaded collection " + item.getListName() , Toast.LENGTH_SHORT).show();

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
