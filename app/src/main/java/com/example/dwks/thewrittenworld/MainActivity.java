package com.example.dwks.thewrittenworld;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**Main Activity page that allows users to log in using Google Sign In or skip straight to main map display
 *
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient mGooogleApiClient;
    private FirebaseAuth firebaseAuth;

    private static final int RC_SIGN_IN = 9001;


    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Constants.notificationsOn=false;
        //Requests permissions for location, camera and storage access on first load
        requestPermissions();
        //Sets up
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        //Starts GeofenceIntentService which also handles the location tracking, needed to find location before opening main map
        Intent service = new Intent(this, GeofenceIntentService.class);

        startService(service);

        SignInButton signIn = (SignInButton) findViewById(R.id.sign_in_button);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        Button signOut = (Button) findViewById(R.id.sign_out_button);
                signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        Button loadScreen = (Button) findViewById(R.id.setup);

        final Intent launchMainApp = new Intent(this, MapDisplay.class);

        if (currentUser != null){
            loadScreen.setText("Start Exploring");
        }


        loadScreen.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                if (firebaseAuth.getCurrentUser() == null){
                    Toast.makeText(getApplicationContext(),"Users Not Signed In Cannot Save", Toast.LENGTH_LONG).show();
                }
                startActivity(launchMainApp);
            }
        });

        //Shared Preferences utility class instance
        ProcessSharedPref sharedPref = new ProcessSharedPref(this);
        if (sharedPref.savedDataExists()){
             sharedPref.loadFromJson(); //loads any saved shared preferences into app
        }


        GoogleSignInOptions googleSign = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();


        mGooogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSign)
                .build();

        if (Constants.geofenceArrayList.size()>0){
            Constants.notificationsOn = true;
        }

    }


    /////CODE FOR CHECKING AND REQUESTING PERMISISONS    ////////////////////////////////////////////////////////////
    //Adapted from Google Sample For Runtime Permissions
    //https://github.com/googlesamples/android-RuntimePermissions
    //
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".

            //Requests permisions for accessing location, using Camera and Writing to External Storage
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.");

            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, 1,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }


    /** GOOGLE SIGN IN PROCESS BOILERPLATE CODE ADPATED FROM THE ANDROID DOCUMENTATION
     *
     * @Link https://github.com/googlesamples/google-services/blob/master/android/signin/app/src/main/java/com/google/samples/quickstart/signin/SignInActivity.java#L51-L55
     *
     */
    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }



    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGooogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideProgressDialog();
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG,"Account name = " + acct.getDisplayName());
            firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(acct.getIdToken(), null));
            //Constants constants = Constants.getInstance();
            //constants.currentUser = acct;
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            Intent toMap = new Intent(this, MapDisplay.class);

            updateUI(true);
            Intent logOut = getIntent();
            if(!logOut.hasExtra("SIGN_OUT")){
                Log.d(TAG,  logOut.toString());
            startActivity(toMap);}
            Log.d(TAG, logOut.toString());
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGooogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        //clear shared prefeneces
        Constants.placeObjects.clear();
        Constants.places.clear();
        new ProcessSharedPref(getApplicationContext()).saveAsJson();

        Auth.GoogleSignInApi.signOut(mGooogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        revokeAccess();

                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        firebaseAuth.signOut();
        Auth.GoogleSignInApi.revokeAccess(mGooogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        recreate();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            Button launch = (Button) findViewById(R.id.setup);
            launch.setText("Start Exploring");

        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            Button launch = (Button) findViewById(R.id.setup);
            launch.setText("Skip Login");
        }
    }




}
