package com.example.dwks.thewrittenworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class PlaceDetailShare extends AppCompatActivity implements View.OnClickListener {

    private PlaceObject placeObject;
    private TextView titleTextView;
    private Button detailMain;
    private Button information;
    private FloatingActionButton tweet;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail_share);
        setUpFields();
        Intent main = getIntent();
        placeObject = main.getParcelableExtra("place");
        title = placeObject.getBookTitle();
        titleTextView.setText(title);

        ImageView imageView = (ImageView) findViewById(R.id.detailImage);

        //Used for default if DB does not contain any preset image
        String googleStreetViewImage = "https://maps.googleapis.com/maps/api/streetview?size=600x300&location="+
                placeObject.getLatitude()+ ","
                +placeObject.getLongitude()
                + "&heading=151.78&pitch=-0.76&key="
                + getString(R.string.GOOGLE_API_KEY);

        Glide.with(getApplicationContext()).load(googleStreetViewImage).into(imageView);

    }

    private void setUpFields(){
        titleTextView = (TextView) findViewById(R.id.location_title_share);
        detailMain = (Button) findViewById(R.id.main_detail);
        detailMain.setOnClickListener(this);
        information = (Button) findViewById(R.id.more_info);
        tweet = (FloatingActionButton) findViewById(R.id.tweetButton);
        tweet.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {

        Intent back = new Intent(this, PlaceDetailScreen.class);
        back.putExtra("Place",placeObject);
        startActivity(back);
    }
}
