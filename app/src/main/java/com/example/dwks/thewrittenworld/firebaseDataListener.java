package com.example.dwks.thewrittenworld;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**Interface for using in Database class for handling Firebase queries
 * Credit to @link https://stackoverflow.com/questions/33723139/wait-firebase-async-retrive-data-in-android
 */

public interface firebaseDataListener {

    void onStart();
    void onSuccess(DataSnapshot dataSnapshot);
    void onFailed(DatabaseError databaseError);
}
