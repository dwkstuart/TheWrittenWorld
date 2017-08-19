package com.example.dwks.thewrittenworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.example.dwks.thewrittenworld.Constants.*;

public class UserFiles extends AppCompatActivity implements View.OnClickListener{

    private Constants constants= getInstance();
    private EditText fileName;
    private Database dbInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_files);
        this.setFields();
        dbInstance = new Database();
    }

    private void setFields(){
        Button save = (Button) findViewById(R.id.saveButton);
        TextView userFiles = (TextView) findViewById(R.id.usersFiles);
        fileName = (EditText) findViewById(R.id.enterFileName);
        Button displayFiles = (Button) findViewById(R.id.displayFileList);
        Button test = (Button) findViewById(R.id.listTest);

        test.setOnClickListener(this);
        save.setOnClickListener(this);
        displayFiles.setOnClickListener(this);


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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.saveButton):

                dbInstance.uploadSaveSelection(fileName.getText().toString(), this.gsonParsingSave());
                //testWrite();
                break;
            case (R.id.displayFileList):
                populateListsField();

                break;
            case (R.id.listTest):
                Intent intent = new Intent(this, ListOfPlaces.class);
                startActivity(intent);
                break;

        }

    }
    private void testWrite(){
        final Set<PlaceObject>[] loadedSet = new Set[]{new TreeSet<PlaceObject>(placeObjects)};
        final int[] pos = new int[1];
            dbInstance.uploadLocation(new firebaseDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                pos[0] = (int) dataSnapshot.getChildrenCount();
                Log.d("On Success", String.valueOf(pos[0]));
                Gson gson = new Gson();
                 loadedSet[0] = gson.fromJson(dataSnapshot.getValue()
                        .toString(), new TypeToken<TreeSet<PlaceObject>>() {}.getType());
            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });
        int value = pos[0];
        TreeSet<PlaceObject> mock = (TreeSet<PlaceObject>) loadedSet[0];
        for (final PlaceObject object: mock){
            dbInstance.loadInfo(object,value);
            value++;
            //Log.d("For loop", object.getBookTitle());


        }
    }

    private String gsonParsingSave(){
        Gson gson = new Gson();
        String jsonTreeSet = gson.toJson(placeObjects);
        return jsonTreeSet;
    }

    private void populateListsField(){
        dbInstance.getUsersLists(new firebaseDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Gson gson = new Gson();
               ArrayList<SavedCollection> files = new ArrayList<>();

                List<String> savedTours = new ArrayList<>();
                for (DataSnapshot list : dataSnapshot.getChildren()) {
                    SavedCollection file = new SavedCollection(list.getKey(),list.getValue().toString());
                    files.add(file);
                }
                //TODO fix this recycler view
                constants.files = files;
                Intent fileListView = new Intent(getApplicationContext(), SavedCollections.class);
                startActivity(fileListView);
            }
            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });

    }

    /**Load the places from selected list
     *
     * @param listName
     */
    private void loadList(String listName){
        dbInstance.getUsersLists(new firebaseDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Gson gson = new Gson();
                Set<PlaceObject> loadedSet = gson.fromJson(dataSnapshot.getValue()
                        .toString(), new TypeToken<TreeSet<PlaceObject>>() {}.getType());
                placeObjects = (TreeSet) loadedSet;
                places.clear();
                for(PlaceObject place: placeObjects){
                    places.put(place.getDb_key(),place);
                }

            }
            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });

    }
}
