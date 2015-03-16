package edu.oakland.ultimatebarista;

//import list, brings in needed functionality
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
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Levels extends GoogleAPI implements View.OnClickListener {
    //Filename for our level progress save file
    String fileName = "ultimatebarista.txt";

    private MediaPlayer mp;

    GridLayout levelGrid = null;

    ImageButton level1,level2,level3,level4,level5,level6,level7,level8,level9,level10,level11,level12 = null;
    Button signOutButton,playTutorialButton = null;

    boolean firstOnResume, startGame = true;

    TextView tutorialText,gameIntroText = null;
    RelativeLayout tutorialLayout,levelsScreenLayout = null;

    ImageView customerHand,trashCan,playerGuideImage = null;

    int level,tutorialPhase = 0;

    //Stringbuffer for reading in save file
    final StringBuffer storedString = new StringBuffer();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        linkUIElements();
        loadSavedInfo();
        setClickListeners();

        disableLevelButtons();
        enableLevelButtons();
        if(level == 0) {
            levelGrid.setVisibility(View.INVISIBLE);
            tutorialLayout.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onClick(View v) {
        //Listener decides what level to load based on which button throws the onClick event
            Intent i = new Intent(this, Game.class);
            i.putExtra("maxLevelCompleted", String.valueOf(level));
        switch (v.getId()) {
            case R.id.playTutorialButton:

                switch (tutorialPhase) {
                    case 0:
                        startGame = false;
                        levelsScreenLayout.setBackgroundResource(R.drawable.tutorialbg);
                        gameIntroText.setText("To create the customer's beverage, press the buttons corresponding to their order in the blackboard. \n \n On the next screen we will show you information about all of the buttons.");
                        playTutorialButton.setText("Continue");
                        tutorialPhase++;
                        break;
                    case 1:
                        playerGuideImage.setVisibility(View.VISIBLE);
                        gameIntroText.setVisibility(View.INVISIBLE);
                        tutorialPhase++;
                        break;
                    case 2:
                        playerGuideImage.setVisibility(View.INVISIBLE);
                        tutorialText.setVisibility(View.VISIBLE);
                        trashCan.setVisibility(View.VISIBLE);
                        customerHand.setVisibility(View.VISIBLE);
                        playTutorialButton.setText("Play Tutorial Level");
                        tutorialPhase++;
                        break;
                    case 3:
                        i.putExtra("maxLevelCompleted",String.valueOf(level));
                        i.putExtra("level","0");
                        i.putExtra("levelTitle","Time to play!");
                        i.putExtra("numOfDrinks","1");
                        startGame = true;
                        break;
                }
                break;
            case R.id.level1button:
                i.putExtra("level","1");
                i.putExtra("levelTitle",getString(R.string.level1title));
                i.putExtra("numOfDrinks",getString(R.string.level1drinks));
                i.putExtra("timeLimit",getString(R.string.level1time));
                break;
            case R.id.level2button:
                i.putExtra("level","2");
                i.putExtra("levelTitle",getString(R.string.level2title));
                i.putExtra("numOfDrinks",getString(R.string.level2drinks));
                i.putExtra("timeLimit",getString(R.string.level2time));
                break;
            case R.id.level3button:
                i.putExtra("level","3");
                i.putExtra("levelTitle",getString(R.string.level3title));
                i.putExtra("numOfDrinks",getString(R.string.level3drinks));
                i.putExtra("timeLimit",getString(R.string.level3time));
                break;
            case R.id.level4button:
                i.putExtra("level","4");
                i.putExtra("levelTitle",getString(R.string.level4title));
                i.putExtra("numOfDrinks",getString(R.string.level4drinks));
                i.putExtra("timeLimit",getString(R.string.level4time));
                break;
            case R.id.level5button:
                i.putExtra("level","5");
                i.putExtra("levelTitle",getString(R.string.level5title));
                i.putExtra("numOfDrinks",getString(R.string.level5drinks));
                i.putExtra("timeLimit",getString(R.string.level5time));
                break;
            case R.id.level6button:
                i.putExtra("level","6");
                i.putExtra("levelTitle",getString(R.string.level6title));
                i.putExtra("numOfDrinks",getString(R.string.level6drinks));
                i.putExtra("timeLimit",getString(R.string.level6time));

                break;
            case R.id.level7button:
                i.putExtra("level","7");
                i.putExtra("levelTitle",getString(R.string.level7title));
                i.putExtra("numOfDrinks",getString(R.string.level7drinks));
                i.putExtra("timeLimit",getString(R.string.level7time));
                break;
            case R.id.level8button:
                i.putExtra("level","8");
                i.putExtra("levelTitle",getString(R.string.level8title));
                i.putExtra("numOfDrinks",getString(R.string.level8drinks));
                i.putExtra("timeLimit",getString(R.string.level8time));
                break;
            case R.id.level9button:
                i.putExtra("level","9");
                i.putExtra("levelTitle",getString(R.string.level9title));
                i.putExtra("numOfDrinks",getString(R.string.level9drinks));
                i.putExtra("timeLimit",getString(R.string.level9time));
                i.putExtra("levelSubtitle",getString(R.string.level9subtitle));
                break;
            case R.id.level10button:
                i.putExtra("level","10");
                i.putExtra("levelTitle",getString(R.string.level10title));
                i.putExtra("numOfDrinks",getString(R.string.level10drinks));
                i.putExtra("timeLimit",getString(R.string.level10time));
                break;
            case R.id.level11button:
                i.putExtra("level","11");
                i.putExtra("levelTitle",getString(R.string.level11title));
                i.putExtra("numOfDrinks",getString(R.string.level11drinks));
                i.putExtra("timeLimit",getString(R.string.level11time));
                break;
            case R.id.level12button:
                i.putExtra("level","12");
                i.putExtra("levelTitle",getString(R.string.level12title));
                i.putExtra("numOfDrinks",getString(R.string.level12drinks));
                i.putExtra("timeLimit",getString(R.string.level12time));
                break;
            case R.id.signOutButton:
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Games.signOut(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                }
                i = new Intent(this, Main.class);
                break;
        }
        if(startGame == true) {
            this.startActivity(i);
            this.finish();
        }
    }

    public void linkUIElements() {
        level1 = (ImageButton) findViewById(R.id.level1button);
        level2 = (ImageButton) findViewById(R.id.level2button);
        level3 = (ImageButton) findViewById(R.id.level3button);
        level4 = (ImageButton) findViewById(R.id.level4button);
        level5 = (ImageButton) findViewById(R.id.level5button);
        level6 = (ImageButton) findViewById(R.id.level6button);
        level7 = (ImageButton) findViewById(R.id.level7button);
        level8 = (ImageButton) findViewById(R.id.level8button);
        level9 = (ImageButton) findViewById(R.id.level9button);
        level10 = (ImageButton) findViewById(R.id.level10button);
        level11 = (ImageButton) findViewById(R.id.level11button);
        level12 = (ImageButton) findViewById(R.id.level12button);
        signOutButton = (Button) findViewById(R.id.signOutButton);

        levelGrid = (GridLayout) findViewById(R.id.levelGrid);
        gameIntroText = (TextView) findViewById(R.id.gameIntroText);
        tutorialText = (TextView) findViewById(R.id.tutorialText);
        tutorialLayout = (RelativeLayout) findViewById(R.id.tutorialLayout);

        playTutorialButton = (Button) findViewById(R.id.playTutorialButton);
        levelsScreenLayout = (RelativeLayout) findViewById(R.id.levelsScreenLayout);

        customerHand = (ImageView) findViewById(R.id.customerHand);
        trashCan = (ImageView) findViewById(R.id.trashCan);
        playerGuideImage = (ImageView) findViewById(R.id.playerGuideImage);

    }

    public void setClickListeners() {
        level1.setOnClickListener(this);
        level2.setOnClickListener(this);
        level3.setOnClickListener(this);
        level4.setOnClickListener(this);
        level5.setOnClickListener(this);
        level6.setOnClickListener(this);
        level7.setOnClickListener(this);
        level8.setOnClickListener(this);
        level9.setOnClickListener(this);
        level10.setOnClickListener(this);
        level11.setOnClickListener(this);
        level12.setOnClickListener(this);
        signOutButton.setOnClickListener(this);
        playTutorialButton.setOnClickListener(this);


    }

    public void disableLevelButtons() {
        level12.setEnabled(false);
        level11.setEnabled(false);
        level10.setEnabled(false);
        level9.setEnabled(false);
        level8.setEnabled(false);
        level7.setEnabled(false);
        level6.setEnabled(false);
        level5.setEnabled(false);
        level4.setEnabled(false);
        level3.setEnabled(false);
        level2.setEnabled(false);
        level1.setEnabled(false);
    }

    public void enableLevelButtons() {

        level = Integer.valueOf(storedString.toString());

        switch (level) {
            case 13:
                level12.setImageResource(R.drawable.coffeecupgreen);
            case 12:
                level12.setEnabled(true);
                if(level == 12)
                    level12.setImageResource(R.drawable.coffeecupdkgray);
                level11.setImageResource(R.drawable.coffeecupgreen);
            case 11:
                level11.setEnabled(true);
                if(level == 11)
                    level11.setImageResource(R.drawable.coffeecupdkgray);
                level10.setImageResource(R.drawable.coffeecupgreen);
            case 10:
                level10.setEnabled(true);
                if(level == 10)
                    level10.setImageResource(R.drawable.coffeecupdkgray);
                level9.setImageResource(R.drawable.coffeecupgreen);
            case 9:
                level9.setEnabled(true);
                if(level == 9)
                    level9.setImageResource(R.drawable.coffeecupdkgray);
                level8.setImageResource(R.drawable.coffeecupgreen);
            case 8:
                level8.setEnabled(true);
                if(level == 8)
                    level8.setImageResource(R.drawable.coffeecupdkgray);
                level7.setImageResource(R.drawable.coffeecupgreen);
            case 7:
                level7.setEnabled(true);
                if(level == 7)
                    level7.setImageResource(R.drawable.coffeecupdkgray);
                level6.setImageResource(R.drawable.coffeecupgreen);
            case 6:
                level6.setEnabled(true);
                if(level == 6)
                    level6.setImageResource(R.drawable.coffeecupdkgray);
                level5.setImageResource(R.drawable.coffeecupgreen);
            case 5:
                level5.setEnabled(true);
                if(level == 5)
                    level5.setImageResource(R.drawable.coffeecupdkgray);
                level4.setImageResource(R.drawable.coffeecupgreen);
            case 4:
                level4.setEnabled(true);
                if(level == 4)
                    level4.setImageResource(R.drawable.coffeecupdkgray);
                level3.setImageResource(R.drawable.coffeecupgreen);
            case 3:
                level3.setEnabled(true);
                if(level == 3)
                    level3.setImageResource(R.drawable.coffeecupdkgray);
                level2.setImageResource(R.drawable.coffeecupgreen);
            case 2:
                level2.setEnabled(true);
                if(level == 2)
                    level2.setImageResource(R.drawable.coffeecupdkgray);

                level1.setImageResource(R.drawable.coffeecupgreen);
            case 1:
                level1.setEnabled(true);
                if(level == 1)
                    level1.setImageResource(R.drawable.coffeecupdkgray);
                break;
        }
    }

    public void startMediaPlayer() {
        mp = MediaPlayer.create(this, R.raw.levelselection);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.start();
            }
        });
    }

    public void loadSavedInfo() {
        //Creates file holder to retrieve save file
        File file = new File(this.getFilesDir(), fileName);
        try {
            //Reads save file
            BufferedReader buf = new BufferedReader(new FileReader(file));
            String strLine = null;
            if((strLine = buf.readLine()) != null) {
                storedString.append(strLine);
            }
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateUI() {
        // Show signed in or signed out view
        if (isSignedIn() && level > 0) {
            findViewById(R.id.signOutButton).setVisibility(View.VISIBLE);

        } else {
            findViewById(R.id.signOutButton).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        updateUI();
    }

    @Override
    protected void onPause() {
        //stop mediaplayer:
        super.onPause();
        if (mp != null) {
            mp.release();
        }

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

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(this, Main.class);
        this.startActivity(i);
        // super.onBackPressed(); // Comment this super call to avoid calling finish()
    }
}

