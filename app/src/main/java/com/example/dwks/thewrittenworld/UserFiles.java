package com.example.dwks.thewrittenworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.example.dwks.thewrittenworld.Constants.placeObjects;

/**Class for handling the saving and loading of user collections
 *
 */
public class UserFiles extends AppCompatActivity implements View.OnClickListener{

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
        ImageButton save = (ImageButton) findViewById(R.id.saveButton);
        fileName = (EditText) findViewById(R.id.enterFileName);
        ImageButton displayFiles = (ImageButton) findViewById(R.id.displayFileList);
        ImageButton filelistdisplay = (ImageButton) findViewById(R.id.listTest);

        filelistdisplay.setOnClickListener(this);
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
                String file = fileName.getText().toString();
                Toast.makeText(this, file + " saved successfully", Toast.LENGTH_SHORT).show();
                fileName.setText("");

                break;
            case (R.id.displayFileList):
                populateListsField();

                break;
            case (R.id.listTest):
                Intent intent = new Intent(this, PlaceObjectList.class);
                ArrayList<PlaceObject> loadedPlaceList = new ArrayList<>();
                loadedPlaceList.addAll(Constants.placeObjects);
                intent.putParcelableArrayListExtra("LIST",loadedPlaceList);
                startActivity(intent);
                break;

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

                for (DataSnapshot list : dataSnapshot.getChildren()) {
                    SavedCollection file = new SavedCollection(list.getKey(),list.getValue().toString());
                    files.add(file);
                }

                Constants.files = files;
                Intent fileListView = new Intent(getApplicationContext(), SavedCollections.class);
                startActivity(fileListView);
            }
            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });

    }

    //////used in populating data from test file///
//    private void testWrite(){
//        final Set<PlaceObject>[] loadedSet = new Set[]{new TreeSet<PlaceObject>(placeObjects)};
//        final int[] pos = new int[1];
//            dbInstance.currentArrayLength(new firebaseDataListener() {
//            @Override
//            public void onStart() {
//
//            }
//
//            @Override
//            public void onSuccess(DataSnapshot dataSnapshot) {
//                pos[0] = (int) dataSnapshot.getChildrenCount();
//                Log.d("On Success", String.valueOf(pos[0]));
//                Gson gson = new Gson();
//                 loadedSet[0] = gson.fromJson(dataSnapshot.getValue()
//                        .toString(), new TypeToken<TreeSet<PlaceObject>>() {}.getType());
//            }
//
//            @Override
//            public void onFailed(DatabaseError databaseError) {
//
//            }
//        });
//        int value = pos[0];
//        TreeSet<PlaceObject> mock = (TreeSet<PlaceObject>) loadedSet[0];
//        for (final PlaceObject object: mock){
//            dbInstance.loadInfo(object,value);
//            value++;
//        }
//    }

}
