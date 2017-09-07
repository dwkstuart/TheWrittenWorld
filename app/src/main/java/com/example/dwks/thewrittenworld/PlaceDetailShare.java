package com.example.dwks.thewrittenworld;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uxcam.UXCam;

import java.io.ByteArrayOutputStream;

/**Class for Place's activity screen with longer description and photo of location
 * plus links to Amazon and Goodreads plus ability to add a photograph
 *
 */
public class PlaceDetailShare extends AppCompatActivity implements View.OnClickListener {

    private PlaceObject placeObject;
    private TextView titleTextView;
    private FloatingActionButton photoUpload;
    private StorageReference imageinstance;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private ImageView imageView;
    private TextView description;
    private String imageURL = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail_share);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpFields();
        Intent main = getIntent();
        placeObject = main.getParcelableExtra("place");
        String title = placeObject.getBookTitle();
        titleTextView.setText(title);
        description.setText(placeObject.getLongDescription());

        //Checks if the user account has a personalised photograph for this location
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            StorageReference imageStorage = storage.getReference().child("images");//gets base level images
            StorageReference placeImages = imageStorage.child(placeObject.getDb_key());
            imageinstance = placeImages.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".png");
        }


        imageView = (ImageView) findViewById(R.id.detailImage);
       getImageURL();

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            photoUpload.setEnabled(false);
            Toast.makeText(this, "Only signed in users can upload images, sorry..", Toast.LENGTH_SHORT).show();
        }


    }

    /**Displays the default GoogleStreetView image if needed
     *
     */
    private void displayDefaultImage(){

        if (imageURL == null) {
            Log.d("Share", "imageURL = null");
            //Used for default if DB does not contain any preset image
            String googleStreetViewImage = "https://maps.googleapis.com/maps/api/streetview?size=600x300&location=" +
                    placeObject.getLatitude() + ","
                    + placeObject.getLongitude()
                    + "&heading=151.78&pitch=-0.76&key="
                    + getString(R.string.GOOGLE_API_KEY);
            imageURL = googleStreetViewImage;

        }

        Glide.with(getApplicationContext()).load(imageURL).into(imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**Searches for the book title on Goodreads booksharing website
     * Launches user web browser
     *
     */
    private void goodreadsSearch(){
        String appendTitle = Uri.encode(placeObject.getBookTitle());

        String query = "https://www.goodreads.com/book/title?id=" + appendTitle;

        Intent search = new Intent(Intent.ACTION_VIEW, Uri.parse(query));
        startActivity(search);
    }

    /**Searches for the book title on Amazon website
     * Launches user web browser
     *
     */
    private void amazonSearch(){
        String appendTitle = Uri.encode(placeObject.getBookTitle());

        String query = "https://www.amazon.co.uk/s/search-alias%3Dstripbooks&field-title=" + appendTitle;
        Intent search = new Intent(Intent.ACTION_VIEW, Uri.parse(query));
        startActivity(search);
    }

    private void setUpFields(){
        titleTextView = (TextView) findViewById(R.id.location_title_share);
        photoUpload = (FloatingActionButton) findViewById(R.id.take_photo);
        photoUpload.setOnClickListener(this);
        ImageButton amazon = (ImageButton)  findViewById(R.id.amazon);
        ImageButton goodReads = (ImageButton) findViewById(R.id.goodreads_search);
        goodReads.setOnClickListener(this);
        amazon.setOnClickListener(this);
        description = (TextView) findViewById(R.id.description);

    }

    /**Helper method to determine the image URL to display
     *
     */
    private void getImageURL() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            imageinstance.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    Glide.with(getApplicationContext()).load(uri.toString()).into(imageView);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });

            if (!imageinstance.getDownloadUrl().isSuccessful()) {
                displayDefaultImage();
            }
        }
        else
            displayDefaultImage();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.goodreads_search:
                goodreadsSearch();
                break;

            case R.id.take_photo:
                dispatchTakePictureIntent();
                //galleryAddPic();
                //uploadP();

                photoUpload.setEnabled(false);//so taking photo until current is uploaded
                break;
            case R.id.amazon:

                amazonSearch();

                break;


        }
    }


    /////////////////////////////////////////////////IMAGE HANDLNG//////////////////////////https://developer.android.com/training/camera/photobasics.html
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        UXCam.allowShortBreakForAnotherApp();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");


            uploadImage(imageBitmap);
        }
    }

    private void uploadImage(Bitmap bitmap){


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100,baos);
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        byte[] data = baos.toByteArray();


        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("name", "Uploaded by " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                .setCustomMetadata("Book", placeObject.getBookTitle())
                .build();

        UploadTask upload = imageinstance.putBytes(data,metadata);
        upload.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                photoUpload.setEnabled(true);

            }
        });
    }


}
