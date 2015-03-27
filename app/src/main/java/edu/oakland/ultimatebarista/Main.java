package edu.oakland.ultimatebarista;

import com.google.android.gms.*;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;


public class Main extends GoogleAPI implements View.OnClickListener {

    Button continueButton = null;
    private MediaPlayer mp;

    boolean firstOnResume = true;


    String fileName = "ultimatebarista.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createSaveFile();

        // Set up button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        continueButton = (Button) findViewById(R.id.continueButton);
        continueButton.setOnClickListener(this);
    }

    private void createSaveFile() {
        File file = new File(this.getFilesDir(), fileName);
        if (!file.exists()) {
            String string = "0";
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(fileName, this.MODE_PRIVATE);
                outputStream.write(string.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }



    private void startMediaPlayer() {
        mp = MediaPlayer.create(this, R.raw.mainscreen);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.start();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                beginUserInitiatedSignIn();
                break;
            case R.id.sign_out_button:
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Games.signOut(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    updateUI();
                }
                break;
            case R.id.continueButton:
                Intent i = new Intent(this, Levels.class);
                startActivity(i);
                this.finish();
                break;
        }
    }

    @Override
    protected void updateUI() {
        // Show signed in or signed out view
        if (isSignedIn()) {
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);

            continueButton.setText("Continue");

        } else {
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            continueButton.setText("Continue Without Sign In");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop mediaplayer:
        if (mp != null) {
            mp.release();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        startMediaPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


}
