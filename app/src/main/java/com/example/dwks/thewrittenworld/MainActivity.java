package com.example.dwks.thewrittenworld;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
//Homepage

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase mDB;

    Button loadScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadScreen = (Button) findViewById(R.id.setup);
        final Intent i = new Intent(this, ChooseAndLoad.class);
        loadScreen.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                startActivity(i);
            }
        });


        UserPlacesDbHelper userPlacesDbHelper = new UserPlacesDbHelper(this);

        mDB = userPlacesDbHelper.getWritableDatabase();
        userPlacesDbHelper.onUpgrade(mDB,1,3);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("places/");
        Log.d("DB", myRef.toString());
        Query recentQuery =myRef.orderByChild("title").equalTo("Oliver Twist");
       recentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Data snapshot =", dataSnapshot.toString());
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String values = postSnapshot.child("db_key").getValue().toString();
                    Log.d ("DB value" , values);
//                    String author = values + " " + String.valueOf((postSnapshot.child("author").getValue()));
//                    String title = values + " " + String.valueOf((postSnapshot.child("title").getValue()));

                    Log.d("Result",postSnapshot.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
