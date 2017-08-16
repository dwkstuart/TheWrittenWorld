package com.example.dwks.thewrittenworld;
//Class to confirm data to use, calls methods to make geofences and has buttons to launch Map and List View

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

//TODO Decide whether to update to just use GeofencingApiClient

public class ChooseAndLoad extends AppCompatActivity implements View.OnClickListener,
        ResultCallback<Status> {


    private CreateGeofence gfG;
    private final static String TAG = ChooseAndLoad.class.getSimpleName();
    //Buttons and text fields

    private Button createFenceButton;
    private TextView infoText;
    private Spinner titleDrop;
    private Spinner authorDrop;
    private AutoCompleteTextView searchTitles;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String selectedTitle ="";
    private String selectedAuthor= "";
   // private ArrayList<PlaceObject> placeObjects;

//    private PendingIntent pendingIntent;
//    private GoogleApiClient googleApiClient;
    //get instance of Firebase database to use for queries
    //private FirebaseDatabase database = FirebaseDatabase.getInstance();


    private ArrayList<String> titleDropdownData = new ArrayList<>();
    private ArrayList<String> authorDropdownData = new ArrayList<>();

    private TreeSet<PlaceObject> foundByAuthor = new TreeSet<>();
    private TreeSet<PlaceObject> foundByTitle = new TreeSet<>();

    private Set<PlaceObject> addedToList = new TreeSet<>();



//    private TreeSet<PlaceObject> nearbyLong = new TreeSet<PlaceObject>();
//    private TreeSet<PlaceObject> nearbyLat = new TreeSet<PlaceObject>();
//    private TreeSet<PlaceObject> nearbyObject;

    Constants constants = Constants.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_and_load);

        setUpButtons();
        getTitles();
        getAuthors();
//        pendingIntent = null;
        GeofencingApi geofencingApi = LocationServices.GeofencingApi;
       // infoText.setText("\n Number of markers set : " + Constants.placeObjects.size());
        //if(constants.lastLocation != null)
        //findNearby();



    }

    /**Gets the distinct book titles on
     *
     */
    private void getTitles(){

        Database db =   new Database();
        db.getUniqueTitles(new firebaseDataListener() {
                @Override
                public void onStart() {
                    Toast.makeText(getApplicationContext(),"Searching Database", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    TreeSet<String> titles = new TreeSet<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        PlaceObject object = new PlaceObject(postSnapshot);
//                        titles.add(object.getBookTitle());
                        foundByTitle.add(object);



                    }
                    Log.d(TAG, "FbT" + foundByTitle.size());
                    Log.d(TAG, "already added" + addedToList.size());
                    foundByTitle.removeAll(addedToList);

                    for (PlaceObject title : foundByTitle){
                        String bookname = title.getBookTitle();
                        titles.add(bookname);
                    }
                    titleDropdownData.clear();
                    titleDropdownData.add("Choose a book");
                    titleDropdownData.addAll(titles);
                }

                @Override
                public void onFailed(DatabaseError databaseError) {

                }

        });


        //Set the contents of the title drop down
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, titleDropdownData);
        titleDrop.setAdapter(adapter);
        titleDrop.setOnItemSelectedListener(new onItemSelectedListener());

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, titleDropdownData);
        searchTitles.setAdapter(adapter);
        searchTitles.setOnClickListener(this);

        }

    private void filterByAuthor(){
            titleDropdownData.clear();
            titleDropdownData.add("Books by " + selectedAuthor);
        TreeSet<PlaceObject> filteredList = foundByTitle;
        TreeSet<String> titles = new TreeSet<>();
            Log.d(TAG, "FbA" + foundByAuthor.toString());
          Log.d(TAG, "FbT" + foundByTitle.toString());

        filteredList.retainAll(foundByAuthor);
            Log.d(TAG,filteredList.toString());
            for(PlaceObject object: filteredList){
                titles.add(object.getBookTitle());
            }
                titleDropdownData.addAll(titles);


    }


        private void getAuthors(){
            Database db =   new Database();
            db.getAuthors(new firebaseDataListener() {
                @Override
                public void onStart() {
                    Toast.makeText(getApplicationContext(),"Searching Database", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    TreeSet<String> authors = new TreeSet<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        PlaceObject object = new PlaceObject(postSnapshot);
                        String name = object.getAuthorFirstName() + " " + object.getAuthorSecondName();
                        authors.add(name);


                    }
                    for (String author:authors){
                        authorDropdownData.add(author);
                    }

                }

                @Override
                public void onFailed(DatabaseError databaseError) {

                }

            });

            //Set the contents of the title drop down
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, authorDropdownData);
            authorDrop.setAdapter(adapter);
            authorDrop.setOnItemSelectedListener(new onItemSelectedListener());


        }

        private void showPicked(){
            TreeSet<String> picked = new TreeSet<String>();
            for (PlaceObject object: addedToList){
                String name = object.getBookTitle();
                picked.add(name);
            }
            infoText.setText("\n Number of Results : " + Constants.placeObjects.size()
            + "\n" + picked.toString());

        }
    /**
     * Helper Method to initialise buttons and set listeners
     */
    private void setUpButtons() {

        Button loadPlacesButton = (Button) findViewById(R.id.loadPlaces);
        createFenceButton = (Button) findViewById(R.id.createGeofences);
        Button deleteFences = (Button) findViewById(R.id.removeGeofences);
        Button loadMap = (Button) findViewById(R.id.ViewMap);
        Button showList = (Button) findViewById(R.id.ViewList);
        Button filterByAuthor = (Button) findViewById(R.id.filter_author);
        infoText = (TextView) findViewById(R.id.InfoBox);
        titleDrop= (Spinner) findViewById(R.id.titleSpinner);
        titleDropdownData.add("Choose a Book");

        authorDrop =(Spinner) findViewById(R.id.author_dropdown);
        authorDropdownData.add("Choose by Author");

        searchTitles = (AutoCompleteTextView) findViewById(R.id.search_title_box);
        searchTitles.setThreshold(2);


        loadPlacesButton.setOnClickListener(this);
        createFenceButton.setOnClickListener(this);
        filterByAuthor.setOnClickListener(this);
        loadMap.setOnClickListener(this);
        showList.setOnClickListener(this);
        deleteFences.setOnClickListener(this);
        if (user == null){
            showList.setEnabled(false);
        }
        if(Constants.placeObjects.isEmpty())
        createFenceButton.setEnabled(false);





    }

    @Override
    public void onResult(@NonNull Status status) {

    }




    /**Method to return the locations from the databases assocaited with a particular title
     *
     * @param title
     */
    private void findBookPlaces(String title){
          Database db = new Database();
        db.getBookPlaces(title, new firebaseDataListener() {

            @Override
            public void onStart() {
                Toast.makeText(getApplicationContext(),"Loading Selection", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                String titlesList ="";

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PlaceObject object = new PlaceObject(postSnapshot);
                    Constants.placeObjects.add(object);
                    Log.d(TAG,"Added to place object list " + object.getBookTitle());
                    //populate HashMap
                    addedToList.add(object);
                    Constants.places.put(object.getDb_key(), object);
                    String newtitle = object.getBookTitle();

                }
                //infoText.setText("\n Number of Results : " + Constants.placeObjects.size());


                if(!Constants.placeObjects.isEmpty())
                    createFenceButton.setEnabled(true);
                getTitles();
                showPicked();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Databse search failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**Method to return the locations from the databases assocaited with a particular title
     *
     * @param selectedAuthor
     */
    private void findBookByAuthor(final String selectedAuthor){
        Database db = new Database();
        titleDropdownData.clear();
        titleDropdownData.add("Books by " + selectedAuthor );
        foundByTitle.clear();
        this.getTitles();

        db.getBooksByAuthor(selectedAuthor, new firebaseDataListener() {

            @Override
            public void onStart() {
                Toast.makeText(getApplicationContext(),"Loading Selection", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                foundByAuthor.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PlaceObject object = new PlaceObject(postSnapshot);
                    foundByAuthor.add(object);

                }
                if(!Constants.placeObjects.isEmpty())
                    createFenceButton.setEnabled(true);

                if(!selectedAuthor.equals("Choose by Author"))
                filterByAuthor();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });
    }


    //Saves data when pause is called in case app is killed in background
    @Override
    protected void onPause() {
        super.onPause();
        ProcessSharedPref processSharedPref = new ProcessSharedPref(this);
        processSharedPref.saveAsJson();
    }

    //saves current hasmmap and treeset if app is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ProcessSharedPref processSharedPref = new ProcessSharedPref(this);
        processSharedPref.saveAsJson();
    }

    @Override
    public void onClick(View v) {
       Log.d(TAG, "On cluck");
//        selectedTitle = searchTitles.getText().toString();
        Log.d(TAG, "Title chosen" + selectedTitle);
        switch (v.getId()) {

            case R.id.loadPlaces:

                String title = String.valueOf(selectedTitle);
                this.findBookPlaces(title);
                searchTitles.clearListSelection();

                break;

            case R.id.createGeofences:
                Log.d(TAG, "Create geofence button pressed");
                gfG = new CreateGeofence(this.getApplicationContext(), "ADD", null);
                Log.d(TAG, gfG.toString());

                break;

            case R.id.removeGeofences:
                if (gfG != null) {
                    Log.d(TAG, gfG.toString());
                    gfG.removeAllGeofence();
                }
                break;

            case R.id.ViewMap:
                Intent map = new Intent(this, MapDisplay.class);
                map.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); //Uses previous version of activity, maintains users position and zoom

                startActivity(map);

                break;

            case R.id.ViewList:
               // TODO temp Intent list = new Intent(this, ListOfPlaces.class);

                Intent save = new Intent(this, ListOfPlaces.class);
                startActivity(save);
                break;
            case R.id.filter_author:
                String authorname = String.valueOf(selectedAuthor);
                this.findBookByAuthor(authorname);

//                filterByAuthor();
                break;

            case R.id.search_title_box:
//                selectedTitle = searchTitles.getText().toString();
//                Log.d(TAG, "Title chosen" + selectedTitle);
                break;

        }


    }


    private ToolBarMenuHandler toolBarMenuHandler = new ToolBarMenuHandler(this);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return toolBarMenuHandler.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        return toolBarMenuHandler.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        invalidateOptionsMenu();
        return toolBarMenuHandler.onOptionsItemSelected(item);
    }





    private class onItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            switch (adapterView.getId()){
                case R.id.author_dropdown:
                    selectedAuthor = adapterView.getSelectedItem().toString();
                    break;
                case R.id.titleSpinner:
                    selectedTitle = adapterView.getSelectedItem().toString();
                    break;



            }


        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
}
