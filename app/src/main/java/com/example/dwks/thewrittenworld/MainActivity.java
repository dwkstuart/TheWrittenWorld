package com.example.dwks.thewrittenworld;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello Donkeybrains");

    }
}
