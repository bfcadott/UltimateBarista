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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;


public class Main extends GoogleAPI implements View.OnClickListener {

    //References to UI elements
    Button continueButton = null;
    private MediaPlayer mp;

    //Used to input level from game file
    final StringBuffer storedString = new StringBuffer();

    //Filename for saved game info
    String fileName = "ultimatebarista.txt";

    //Intialize User's level
    int level = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sw600dp);


        createSaveFile();
        loadSavedInfo();

        // Set up button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        continueButton = (Button) findViewById(R.id.continueButton);
        continueButton.setOnClickListener(this);
    }


    /*
    * createSaveFile checks if the game's save file exists. If the file exists, it does nothing.
    * If the file does not exist, the file is created and the user's level is set to 0 in the file.
     */
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

    /*
    * loadSavedInfo retrieves the user's current level from the file stored on their device.
     */
    public void loadSavedInfo() {
        //Creates file holder to retrieve save file
        File file = new File(this.getFilesDir(), fileName);
        try {
            //Reads save file
            BufferedReader buf = new BufferedReader(new FileReader(file));
            String strLine = null;
            if((strLine = buf.readLine()) != null) {
                storedString.append(strLine);
                level = Integer.valueOf(storedString.toString());
            }
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * startMediaPlayer begins the audio playback of the Game's music.
     */
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

    /*
    * onClick listener handles the button clicks for the buttons on the Main screen.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //Calls the inherited GoogleAPI method to handle User sign in
            case R.id.sign_in_button:
                beginUserInitiatedSignIn();
                break;
            //Calls the inherited GoogleAPI method to handle User sign out
            case R.id.sign_out_button:
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Games.signOut(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    updateUI();
                }
                break;
            //Switches to the Levels activity
            case R.id.continueButton:
                Intent i = new Intent(this, Levels.class);
                startActivity(i);
                this.finish();
                break;
        }
    }

    /*
    * updateUI is called after the user has signed in or signed out of the Google Play Game Services.
    * If the user signed in:
    *  • the sign in button is replaced with a sign out button
    *  • Continue button updates to say "continue"
    *  • UserAchievements innerclass is loaded to handle level synchronization.
    * If the user signs out:
    *  • the sign out button is replaced with a sign in button
    *  • Continue button updates to say "Continue Without Sign In"
    */
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

    /*
    * Modified version of code provided from:
    * http://stackoverflow.com/questions/21550241/android-how-to-check-if-an-achievement-has-been-unlocked
    *
    * This class retrieves all of the user's achievmenets from Google Play Game Services.
    *
    * Then, it iterates through each achievement to see if it is unlocked.
    *
    * If the achievmenet is unlocked:
    *   • Compare to see if it matches one of the Ultimate Barista achievements
    *     using syncLevelProgress method
    *   • If there is a match, level corresponding to achievement is returned.
    *   • Level progress is updated to reflect the Google Play Game Services progress only if
    *     it is a higher level than what is stored in the save game file.
     */

    class UserAchievements implements ResultCallback<Achievements.LoadAchievementsResult> {
        @Override
        public void onResult(Achievements.LoadAchievementsResult arg0) {

            //Default value for syncLevel, -1 means there are no achievements earned
            int syncLevel = -1;
            Achievement ach;
            AchievementBuffer achievementBuffer = arg0.getAchievements();
            Iterator<Achievement> aIterator = achievementBuffer.iterator();

            //loop through each achivement until we have an unlocked match
            while (aIterator.hasNext()) {
                ach = aIterator.next();
                if(ach.getState() == Achievement.STATE_UNLOCKED) {
                    syncLevel = syncLevelProgress(ach);
                    if(syncLevel > -1) {
                        achievementBuffer.close();
                        //Only update the saved game file if the level progress is higher
                        if(syncLevel > level) {
                            level = syncLevel;
                            updateSaveFile();
                        }
                        break;
                    }
                }

            }
        }

        //Compares the input achievement with Ultimate Barista achievements to see if there is a match
        //If there is, level # is returned. Otherwise, -1 for no match.
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

    }

    /*
    * updateSaveFile writes the user's current level to the save file
     */
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
        super.onResume();
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
