package com.example.dwks.thewrittenworld;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**Interface for using in Database class for handling Firebase queries
 * Credit to @link https://stackoverflow.com/questions/33723139/wait-firebase-async-retrive-data-in-android
 * Created by User on 05/08/2017.
 */

public interface firebaseDataListener {

    public void onStart();
    public void onSuccess(DataSnapshot dataSnapshot);
    public void onFailed(DatabaseError databaseError);
}
