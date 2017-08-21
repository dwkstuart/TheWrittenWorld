package com.example.dwks.thewrittenworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListOfPlaces extends AppCompatActivity implements PlaceDetailFragment.OnListFragmentInteractionListener{

    private final String TAG = ListOfPlaces.class.getSimpleName();
    private ArrayList<PlaceObject> temp = new ArrayList<>();
    private FloatingActionButton load;
    private String collectionTitle = "Your Current Tour";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_placedetail_list);
        final TextView filename = (TextView) findViewById(R.id.list_title);
        filename.setText(collectionTitle);
        load = (FloatingActionButton) findViewById(R.id.load_list);

        Intent open = getIntent();
        Log.d(TAG, open.toString());
        if (open.hasExtra("LIST")){
            temp = open.getParcelableArrayListExtra("LIST");
        }
        if (open.hasExtra("FILE_NAME")){
            Log.d(TAG, open.toString());
            collectionTitle =open.getStringExtra("FILE_NAME");
            filename.setText(collectionTitle);
        } else load.setVisibility(View.GONE);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        PlaceDetailRecyclerViewAdapter adapter = new PlaceDetailRecyclerViewAdapter(temp, this);
        recyclerView.setAdapter(adapter);



        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.placeObjects.clear();
                Constants.placeObjects.addAll(temp);
                for (PlaceObject object:temp){
                    Constants.places.put(object.getDb_key(),object);
                }

                Toast.makeText(getApplicationContext(),"Loaded collection " + collectionTitle , Toast.LENGTH_SHORT).show();

            }
        });
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
