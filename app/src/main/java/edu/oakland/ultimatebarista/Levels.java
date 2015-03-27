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

    GridLayout levelGrid, page2levelGrid = null;

    ImageButton level1,level2,level3,level4,level5,level6,level7,level8,level9,level10,level11,
    level12,level13, level14, level15,level16,level17,level18,level19,level20 = null;
    Button signOutButton,playTutorialButton,switchPageButton = null;

    boolean startGame = true;

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
        if(level < 13) {
            switchPageButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        //Listener decides what level to load based on which button throws the onClick event
            Intent i = new Intent(this, Game.class);
            i.putExtra("maxLevelCompleted", String.valueOf(level));
        startGame = true;
        switch (v.getId()) {
            case R.id.switchPageButton:
                startGame = false;
                if(page2levelGrid.getVisibility() == View.VISIBLE) {
                    page2levelGrid.setVisibility(View.GONE);
                    levelGrid.setVisibility(View.VISIBLE);
                    switchPageButton.setText("Next Levels");
                } else {
                    levelGrid.setVisibility(View.GONE);
                    page2levelGrid.setVisibility(View.VISIBLE);
                    switchPageButton.setText("Previous Levels");
                }
                break;
            case R.id.playTutorialButton:
                startGame = false;
                switch (tutorialPhase) {
                    case 0:
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
                        i.putExtra("level", "0");
                        i.putExtra("maxLevelCompleted",String.valueOf(level));
                        startGame = true;
                        break;
                }
                break;
            case R.id.level1button:
                i.putExtra("level","1");
                break;
            case R.id.level2button:
                i.putExtra("level","2");
                break;
            case R.id.level3button:
                i.putExtra("level","3");
                break;
            case R.id.level4button:
                i.putExtra("level","4");
                break;
            case R.id.level5button:
                i.putExtra("level","5");
                break;
            case R.id.level6button:
                i.putExtra("level","6");
                break;
            case R.id.level7button:
                i.putExtra("level","7");
                break;
            case R.id.level8button:
                i.putExtra("level","8");
                break;
            case R.id.level9button:
                i.putExtra("level","9");
                break;
            case R.id.level10button:
                i.putExtra("level","10");
                break;
            case R.id.level11button:
                i.putExtra("level","11");
                break;
            case R.id.level12button:
                i.putExtra("level","12");
                break;
            case R.id.level13button:
                i.putExtra("level","13");
                break;
            case R.id.level14button:
                i.putExtra("level","14");
                break;
            case R.id.level15button:
                i.putExtra("level","15");
                break;
            case R.id.level16button:
                i.putExtra("level","16");
                break;
            case R.id.level17button:
                i.putExtra("level","17");
                break;
            case R.id.level18button:
                i.putExtra("level","18");
                break;
            case R.id.level19button:
                i.putExtra("level","19");
                break;
            case R.id.level20button:
                i.putExtra("level","20");
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
        level13 = (ImageButton) findViewById(R.id.level13button);
        level14 = (ImageButton) findViewById(R.id.level14button);
        level15 = (ImageButton) findViewById(R.id.level15button);
        level16 = (ImageButton) findViewById(R.id.level16button);
        level17 = (ImageButton) findViewById(R.id.level17button);
        level18 = (ImageButton) findViewById(R.id.level18button);
        level19 = (ImageButton) findViewById(R.id.level19button);
        level20 = (ImageButton) findViewById(R.id.level20button);

        switchPageButton = (Button) findViewById(R.id.switchPageButton);
        signOutButton = (Button) findViewById(R.id.signOutButton);

        levelGrid = (GridLayout) findViewById(R.id.levelGrid);
        page2levelGrid = (GridLayout) findViewById(R.id.page2levelGrid);
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
        level13.setOnClickListener(this);
        level14.setOnClickListener(this);
        level15.setOnClickListener(this);
        level16.setOnClickListener(this);
        level17.setOnClickListener(this);
        level18.setOnClickListener(this);
        level19.setOnClickListener(this);
        level20.setOnClickListener(this);
        signOutButton.setOnClickListener(this);
        playTutorialButton.setOnClickListener(this);
        switchPageButton.setOnClickListener(this);
    }

    public void disableLevelButtons() {
        level20.setEnabled(false);
        level19.setEnabled(false);
        level18.setEnabled(false);
        level17.setEnabled(false);
        level16.setEnabled(false);
        level15.setEnabled(false);
        level14.setEnabled(false);
        level13.setEnabled(false);
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
        switch (level) {
            case 21:
                level20.setImageResource(R.drawable.coffeecupgreen);
            case 20:
                level20.setEnabled(true);
                if(level == 20)
                    level20.setImageResource(R.drawable.coffeecupdkgray);
                level19.setImageResource(R.drawable.coffeecupgreen);
            case 19:
                level19.setEnabled(true);
                if(level == 19)
                    level19.setImageResource(R.drawable.coffeecupdkgray);
                level18.setImageResource(R.drawable.coffeecupgreen);
            case 18:
                level18.setEnabled(true);
                if(level == 18)
                    level18.setImageResource(R.drawable.coffeecupdkgray);
                level17.setImageResource(R.drawable.coffeecupgreen);
            case 17:
                level17.setEnabled(true);
                if(level == 17)
                    level17.setImageResource(R.drawable.coffeecupdkgray);
                level16.setImageResource(R.drawable.coffeecupgreen);
            case 16:
                level16.setEnabled(true);
                if(level == 16)
                    level16.setImageResource(R.drawable.coffeecupdkgray);
                level15.setImageResource(R.drawable.coffeecupgreen);
            case 15:
                level15.setEnabled(true);
                if(level == 15)
                    level15.setImageResource(R.drawable.coffeecupdkgray);
                level14.setImageResource(R.drawable.coffeecupgreen);
            case 14:
                level14.setEnabled(true);
                if(level == 14)
                    level14.setImageResource(R.drawable.coffeecupdkgray);
                level13.setImageResource(R.drawable.coffeecupgreen);
            case 13:
                levelGrid.setVisibility(View.GONE);
                page2levelGrid.setVisibility(View.VISIBLE);
                switchPageButton.setText("Previous Levels");
                level13.setEnabled(true);
                if(level == 13)
                    level13.setImageResource(R.drawable.coffeecupdkgray);
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
                level = Integer.valueOf(storedString.toString());
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
            //unlockAchievement();
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
    private void unlockAchievement() {
        switch(level - 1) {
            case 1:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQAQ");
                break;
            case 2:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQAg");
                break;
            case 3:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQAw");
                break;
            case 4:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQBA");
                break;
            case 5:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQBQ");
                break;
            case 6:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQCA");
                break;
            case 7:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQCQ");
                break;
            case 8:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQCg");
                break;
            case 9:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQCw");
                break;
            case 10:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQDA");
                break;
            case 11:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQDQ");
                break;
            case 12:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQDg");
                break;
            case 13:
                break;
            case 14:
                break;
            case 15:
                break;
            case 16:
                break;
            case 17:
                break;
            case 18:
                break;
            case 19:
                break;
            case 20:
                break;
        }
    }
}

