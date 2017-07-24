package com.example.dwks.thewrittenworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
//Homepage

public class MainActivity extends AppCompatActivity {

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
    }
}