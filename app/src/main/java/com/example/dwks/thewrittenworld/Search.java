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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.TreeSet;

/**Finding locations associated with a book
 * Enables filtering by author via drop downs
 *
 */

public class Search extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener,
        ResultCallback<Status> {


    private GeofenceHandler geofenceHandler;
    private final static String TAG = Search.class.getSimpleName();
    private Toast mToast;

    //Buttons and text fields

    private Spinner titleDrop;
    private Spinner authorDrop;
    private AutoCompleteTextView searchTitles;
    private TextView list;

    private String selectedAuthor= "";

    //Saved instance text keys
    private final String  SELECTED_TITLES = "SELECLTED_TITLES";

    private ArrayList<String> titleDropdownData = new ArrayList<>();
    private ArrayList<String> authorDropdownData = new ArrayList<>();

    private TreeSet<PlaceObject> foundByAuthor = new TreeSet<>();
    private TreeSet<PlaceObject> foundByTitle = new TreeSet<>();

    private TreeSet<PlaceObject> addedToList = new TreeSet<>();
    private ArrayList<PlaceObject> parcelList = new ArrayList<>();

    //tracks if user is interacting with screen or database is updating screen
    private boolean userTouch;

    private Database db;



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setUpButtons();
        db =   new Database();

        //GeofencingApi geofencingApi = LocationServices.GeofencingApi;

        ProcessSharedPref processSharedPref = new ProcessSharedPref(this);

        if (savedInstanceState == null && processSharedPref.savedListExists() ){
            ArrayList<PlaceObject> temp;
            temp = processSharedPref.loadAddedTitles();
            Log.d(TAG, String.valueOf(temp.size()));
            addedToList.addAll(temp);
            addedToList.addAll(Constants.placeObjects);

        }
        if (Constants.placeObjects.isEmpty()){
            addedToList.clear();
            showPicked();
        }
        getTitles();
        getAuthors();


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        parcelList = savedInstanceState.getParcelableArrayList(SELECTED_TITLES);
        addedToList.addAll(parcelList);

        showPicked();
    }

    /**Gets the distinct book titles of the locations available on the database,
     * excludes those that have already been added by the user
     * Populates Spinner/dropdown
     */
    private void getTitles(){

        db.getUniqueTitles(new firebaseDataListener() {
                @Override
                public void onStart() {


                }

                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    TreeSet<String> titles = new TreeSet<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        PlaceObject object = new PlaceObject(postSnapshot);
                        foundByTitle.add(object);
                    }

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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_custom_style, titleDropdownData);
        titleDrop.setAdapter(adapter);
        titleDrop.setOnItemSelectedListener(new onItemSelectedListener());

        ArrayAdapter<String> adapterSearch = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, titleDropdownData);
        searchTitles.setAdapter(adapterSearch);
        searchTitles.setOnClickListener(this);

        showPicked();


    }

    /**Method that filters the books in the dropdown list by the selected author
     *
     */
    private void filterByAuthor(){
            titleDropdownData.clear();
        TreeSet<PlaceObject> filteredList = foundByTitle;
        TreeSet<String> titles = new TreeSet<>();

        filteredList.retainAll(foundByAuthor);
            Log.d(TAG,filteredList.toString());
            for(PlaceObject object: filteredList){
                titles.add(object.getBookTitle());
            }
        titleDropdownData.add("Books by " + selectedAuthor);

        titleDropdownData.addAll(titles);

    }


    /**Method to return a list of distinct authors in the database,
     *  used to populate a dropdown for giving users chance to filter by author
     *
     */
    private void getAuthors(){

            db.getAuthors(new firebaseDataListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    TreeSet<String> authors = new TreeSet<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        PlaceObject object = new PlaceObject(postSnapshot);
                        String name = object.getAuthorName();
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
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_custom_style, authorDropdownData);
            authorDrop.setAdapter(adapter);
            authorDrop.setOnItemSelectedListener(new onItemSelectedListener());


        }

    /**Displays a notification when user picks and title and adds it to the
     * lst of previously picked titles displayed at bottom of the screen
     *
     */
    private void showPicked(){
            TreeSet<String> picked = new TreeSet<String>();
            for (PlaceObject object: addedToList){
                String name = object.getBookTitle();
                picked.add(name);
            }
            list.setText("");
            for(String title: picked){
                list.append(title);
                list.append("\n");
            }
            if (!picked.isEmpty()){
            if(mToast != null)
                mToast.cancel();
            mToast = Toast.makeText(getApplicationContext(), "Added locations from slected book to current selection", Toast.LENGTH_SHORT);
            mToast.show();}

        }


    /**
     * Helper Method to initialise buttons and set listeners
     */
    private void setUpButtons() {
        list = (TextView) findViewById(R.id.selected_titles);

        titleDropdownData.add("Choose a Book");
        titleDrop= (Spinner) findViewById(R.id.titleSpinner);
        Button viewSelection = (Button) findViewById(R.id.view_selection);
        viewSelection.setOnClickListener(this);
        authorDrop =(Spinner) findViewById(R.id.author_dropdown);
        authorDropdownData.add("Filter by Author");

        searchTitles = (AutoCompleteTextView) findViewById(R.id.search_title_box);
        searchTitles.setThreshold(2);
        searchTitles.setOnItemClickListener(this);

   }

    @Override
    public void onResult(@NonNull Status status) {

    }




    /**Method to return the locations from the databases assocaited with a particular title
     *
     * @param title of book
     */
    private void findBookPlaces(String title){
        db.getBookPlaces(title, new firebaseDataListener() {

            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PlaceObject object = new PlaceObject(postSnapshot);

                    //populate HashMap
                    addedToList.add(object);

                }

                 getTitles();
                showPicked();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Databse search failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**Method to return the titles of the books by a particular author
     * Sets the title drop down to just display books by that particular author
     *
     * @param selectedAuthor
     */
    private void findBookByAuthor(final String selectedAuthor){
        titleDropdownData.clear();
        titleDropdownData.add("Books by " + selectedAuthor );
        foundByTitle.clear();
        this.getTitles();

        db.getBooksByAuthor(selectedAuthor, new firebaseDataListener() {

            @Override
            public void onStart() {
                if(mToast != null)
                    mToast.cancel();
                mToast = Toast.makeText(getApplicationContext(),"Loading Selection", Toast.LENGTH_LONG);
                        mToast.show();
            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                foundByAuthor.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PlaceObject object = new PlaceObject(postSnapshot);
                    foundByAuthor.add(object);

                }

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
        handleSaving();

    }

    //saves current hashmap and treeset if app is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handleSaving();
    }

    /**
     * Helper method for dealing with saving variables when activity is destroy, e.g. on rotation
     */
    private void handleSaving(){
        ProcessSharedPref processSharedPref = new ProcessSharedPref(this);
        processSharedPref.saveAsJson();
        ArrayList<PlaceObject> temp = new ArrayList<>();
        temp.addAll(addedToList);
        processSharedPref.saveAddedTitles(temp);
    }
    private int getTotalVisited(){
        int visited = 0;
        for(PlaceObject place: addedToList){
            if(place.isVisited()){
                visited++;
            }
        }
        return visited;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.view_selection:
                Intent selectionview = new Intent(this, PlaceObjectList.class);

                parcelList.clear(); //clear list to avoid duplicates
                parcelList.addAll(addedToList);//add currently added files
                int visited = getTotalVisited();
                selectionview.putExtra(getString(R.string.number_visited), visited);
                selectionview.putExtra("LIST",parcelList);
                selectionview.putExtra("FILE_NAME", "Current choices");
                startActivity(selectionview);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        this.findBookPlaces(searchTitles.getText().toString());
        searchTitles.clearListSelection();
        searchTitles.setText("");
    }

    /**Detects if user is interacting with screen or not,
     *required to handle problem with items being selected when database updates alter content of spinner
     */
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userTouch = true;
    }

    /**Responds to user selecting and item from either dropdown
     *
     */
    private class onItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            switch (adapterView.getId()){
                case R.id.author_dropdown:
                    selectedAuthor = adapterView.getSelectedItem().toString();
                    if(userTouch)// to stop the item selected list
                        findBookByAuthor(selectedAuthor);
                    userTouch = false;

                    if(mToast != null)
                        mToast.cancel();
                    mToast = Toast.makeText(getApplicationContext(), "Choose book title from drop down", Toast.LENGTH_SHORT);
                    mToast.show();

                    break;
                case R.id.titleSpinner:
                    Log.d(TAG,adapterView.getSelectedItem().toString());
                    String selectedTitle = adapterView.getSelectedItem().toString();
                    if(userTouch)
                        findBookPlaces(selectedTitle);
                    userTouch = false;



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
        parcelList.addAll(addedToList);
        outState.putParcelableArrayList(SELECTED_TITLES, parcelList);
        String TITLES = "choosenTitles";
        outState.putString(TITLES,list.getText().toString());
        handleSaving();
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

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


}
