package com.example.dwks.thewrittenworld;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class UserFiles extends AppCompatActivity implements View.OnClickListener{

    Constants constants= Constants.getInstance();
    Button save;
    Button displayFiles;
    TextView userFiles;
    EditText fileName;
    Database dbInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_files);
        this.setFields();
        dbInstance = new Database();
    }

    private void setFields(){
        save = (Button) findViewById(R.id.saveButton);
        userFiles =(TextView) findViewById(R.id.usersFiles);
        fileName = (EditText) findViewById(R.id.enterFileName);
        displayFiles = (Button) findViewById(R.id.displayFileList);

        save.setOnClickListener(this);
        displayFiles.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.saveButton):
                Log.d("DB SAVE","Save button pressed");
                dbInstance.uploadSaveSelection(fileName.getText().toString(), this.gsonParsingSave());
                break;
            case (R.id.displayFileList):
                populateListsField();
                break;

        }

    }

    private String gsonParsingSave(){
        Gson gson = new Gson();
        String jsonTreeSet = gson.toJson(constants.placeObjects);
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
                List<String> savedTours = new ArrayList<String>();

                for (DataSnapshot list : dataSnapshot.getChildren()) {
                    Set<PlaceObject> set = gson.fromJson(list.getValue().toString(), new TypeToken<TreeSet<PlaceObject>>() {}.getType());
                    Log.d("List", list.getKey() + "number of places = " +set.size());
                    userFiles.append(list.getKey() + " has " + set.size() + " places in it \n");
                    savedTours.add(list.getKey());
                }
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
                Set<PlaceObject> loadedSet = gson.fromJson(dataSnapshot.getValue().toString(), new TypeToken<TreeSet<PlaceObject>>() {}.getType());
                constants.placeObjects = (TreeSet) loadedSet;
                constants.places.clear();
                for(PlaceObject place: constants.placeObjects){
                    constants.places.put(place.getDb_key(),place);
                }

            }
            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });

    }
}
