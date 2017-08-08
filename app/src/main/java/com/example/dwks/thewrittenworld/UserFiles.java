package com.example.dwks.thewrittenworld;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

public class UserFiles extends AppCompatActivity implements View.OnClickListener{

    Constants constants= Constants.getInstance();
    Button save;
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

        save.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.saveButton):
                Log.d("DB SAVE","Save button pressed");
                dbInstance.uploadSaveSelection(fileName.getText().toString(), this.gsonParsingSave());
                break;
        }

    }

    private String gsonParsingSave(){
        Gson gson = new Gson();
        String jsonTreeSet = gson.toJson(constants.placeObjects);
        return jsonTreeSet;
    }
}
