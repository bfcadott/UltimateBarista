package edu.oakland.ultimatebarista;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;


public class Main extends GoogleAPI implements View.OnClickListener {

    Button continueButton = null;
    private MediaPlayer mp;

    boolean firstOnResume = true;


    String fileName = "ultimatebarista.txt";
    int level = 0;

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
            Games.Achievements.load(mGoogleApiClient, false).setResultCallback(new UserAchievements());
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



class UserAchievements implements ResultCallback<Achievements.LoadAchievementsResult> {
    @Override
    public void onResult(Achievements.LoadAchievementsResult arg0) {
        int syncLevel = -1;
        Achievement ach;
        AchievementBuffer achievementBuffer = arg0.getAchievements();
        Iterator<Achievement> aIterator = achievementBuffer.iterator();

        while (aIterator.hasNext()) {
            ach = aIterator.next();
            if(ach.getState() == Achievement.STATE_UNLOCKED) {
                syncLevel = syncLevelProgress(ach);
                if(syncLevel > -1) {
                    achievementBuffer.close();
                    level = syncLevel;
                    updateSaveFile();
                    break;
                }
            }

        }
    }
}

    protected int syncLevelProgress(Achievement ach) {
        switch(ach.getAchievementId()) {
            case "CgkIto6VvK4ZEAIQFg":
                return 21;
            case "CgkIto6VvK4ZEAIQFQ":
                return 20;
            case "CgkIto6VvK4ZEAIQFA":
                return 19;
            case "CgkIto6VvK4ZEAIQEw":
                return 18;
            case "CgkIto6VvK4ZEAIQEg":
                return 17;
            case "CgkIto6VvK4ZEAIQEQ":
                return 16;
            case "CgkIto6VvK4ZEAIQEA":
                return 15;
            case "CgkIto6VvK4ZEAIQDw":
                return 14;
            case "CgkIto6VvK4ZEAIQDg":
                return 13;
            case "CgkIto6VvK4ZEAIQDQ":
                return 12;
            case "CgkIto6VvK4ZEAIQDA":
                return 11;
            case "CgkIto6VvK4ZEAIQCw":
                return 10;
            case "CgkIto6VvK4ZEAIQCg":
                return 9;
            case "CgkIto6VvK4ZEAIQCQ":
                return 8;
            case "CgkIto6VvK4ZEAIQCA":
                return 7;
            case "CgkIto6VvK4ZEAIQBQ":
                return 6;
            case "CgkIto6VvK4ZEAIQBA":
                return 5;
            case "CgkIto6VvK4ZEAIQAw":
                return 4;
            case "CgkIto6VvK4ZEAIQAg":
                return 3;
            case "CgkIto6VvK4ZEAIQAQ":
                return 2;
            default:
                return -1;
        }
    }

    public void updateSaveFile() {
        String string = String.valueOf(level);
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
