package edu.oakland.ultimatebarista;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

/**
 * Code provided by Google: https://developers.google.com/games/services/android/quickstart
 */
public abstract class GoogleAPI extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /// Client used to interact with Google APIs.
    public GoogleApiClient mGoogleApiClient;

    public static final String TAG = "GoogleAPI";


    // Request code used to invoke sign-in UI.
    private static final int RC_SIGN_IN = 9001;



    // Progress Dialog used to display loading messages.
    private ProgressDialog mProgressDialog;

    // True when the application is attempting to resolve a sign-in error that has a possible
    // resolution,
    private boolean mIsResolving = false;

    // True immediately after the user clicks the sign-in button/
    private boolean mSignInClicked = false;

    // True if we want to automatically attempt to sign in the user at application start.
    private boolean mAutoStartSignIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES) // Games
                .addScope(Drive.SCOPE_FILE)
                .addApi(AppStateManager.API).addScope(AppStateManager.SCOPE_APP_STATE) // AppState
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_APPFOLDER) // SavedGames
                .build();
    }

    public void beginUserInitiatedSignIn() {
        Log.d(TAG, "beginUserInitiatedSignIn");


        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    public boolean isSignedIn() {
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
        if (mIsResolving) {
            // The application is attempting to resolve this connection failure already.
            Log.d(TAG, "onConnectionFailed: already resolving");
            return;
        }

        if (mSignInClicked || mAutoStartSignIn) {
            mSignInClicked = false;
            mAutoStartSignIn = false;

            // Attempt to resolve the connection failure.
            Log.d(TAG, "onConnectionFailed: begin resolution.");
            mIsResolving = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
                    connectionResult, RC_SIGN_IN, "OTHER ERROR");
        }

        updateUI();
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected");
        updateUI();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: " + i);
        mGoogleApiClient.connect();
        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: RC_SIGN_IN, resultCode = " + resultCode);
            mSignInClicked = false;
            mIsResolving = false;

            if (resultCode == RESULT_OK) {
                // Sign-in was successful, connect the API Client
                Log.d(TAG, "onActivityResult: RC_SIGN_IN (OK)");
                mGoogleApiClient.connect();
            } else {
                // There was an error during sign-in, display a Dialog with the appropriate message
                // to the user.
                Log.d(TAG, "onActivityResult: RC_SIGN_IN (Error)");
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode, 1);
            }
        }
    }

    protected abstract void updateUI();

}
