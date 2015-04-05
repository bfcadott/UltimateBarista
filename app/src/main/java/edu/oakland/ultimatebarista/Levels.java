package edu.oakland.ultimatebarista;

//import list, brings in needed functionality
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.games.Games;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Levels extends GoogleAPI implements View.OnClickListener {

    //Filename for our level progress save file
    String fileName = "ultimatebarista.txt";

    /*
    * Lots of references for GUI elements
     */
    private MediaPlayer mp;

    GridLayout levelGrid, page2levelGrid = null;

    ImageButton level1,level2,level3,level4,level5,level6,level7,level8,level9,level10,level11,
    level12,level13, level14, level15,level16,level17,level18,level19,level20 = null;
    Button signOutButton,playTutorialButton,switchPageButton = null;

    boolean startGame = true;

    TextView tutorialText,gameIntroText = null;
    RelativeLayout tutorialLayout,levelsScreenLayout = null;

    ImageView customerHand,trashCan,playerGuideImage = null;

    //Initialize level and tutorialPhase
    int level,tutorialPhase = 0;

    //Stringbuffer for reading in save file
    final StringBuffer storedString = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels_sw600dp);

        linkUIElements();
        loadSavedInfo();
        setClickListeners();

        disableLevelButtons();
        enableLevelButtons();
        //If the level is 0, then the user must go through the Tutorial
        if(level == 0) {
            levelGrid.setVisibility(View.INVISIBLE);
            tutorialLayout.setVisibility(View.VISIBLE);
        }
        //If the level is above 13, then show the next page button.
        if(level < 13) {
            switchPageButton.setVisibility(View.INVISIBLE);
        }
    }

    /*
    * onClick handles the button clicks for the various UI elements
     */
    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, Game.class);
        i.putExtra("maxLevelCompleted", level);

        //This boolean is used to determine if the game should progress to the Game activity
        startGame = true;

        switch (v.getId()) {
            //Switches between levels page1 and page2.
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

            //This button progresses the user through the Tutorial by using the int value of tutorialPhase
            //to determine which of the 4 screens should be displayed.
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
                        gameIntroText.setText("First, select a cup, then milk, then add the rest. \n \n  Lastly, if required, add whipped cream! \n \n" +
                                "After adding whipped cream you are unable to modify the drink.");
                        playerGuideImage.setVisibility(View.INVISIBLE);
                        gameIntroText.setVisibility(View.VISIBLE);
                        tutorialPhase++;
                        break;
                    case 3:
                        gameIntroText.setVisibility(View.INVISIBLE);
                        playerGuideImage.setVisibility(View.INVISIBLE);
                        tutorialText.setVisibility(View.VISIBLE);
                        trashCan.setVisibility(View.VISIBLE);
                        customerHand.setVisibility(View.VISIBLE);
                        tutorialPhase++;
                        break;
                    case 4:
                        tutorialText.setVisibility(View.INVISIBLE);
                        trashCan.setVisibility(View.INVISIBLE);
                        customerHand.setVisibility(View.INVISIBLE);
                        gameIntroText.setVisibility(View.VISIBLE);
                        gameIntroText.setText("An \"X\" will appear if you give the customer an incorrect drink. \n\n" +
                                "If you give customers 3 incorrect drinks, or run out of time, you fail the level!");
                        playTutorialButton.setText("Play Tutorial Level");
                        tutorialPhase++;
                        break;
                    case 5:
                        i.putExtra("level", 0);
                        i.putExtra("maxLevelCompleted",level);
                        startGame = true;
                        break;
                }
                break;

            /*
            * level buttons 1-20 all pass the level # to the Game activity
             */
            case R.id.level1button:
                i.putExtra("level",1);
                break;
            case R.id.level2button:
                i.putExtra("level",2);
                break;
            case R.id.level3button:
                i.putExtra("level",3);
                break;
            case R.id.level4button:
                i.putExtra("level",4);
                break;
            case R.id.level5button:
                i.putExtra("level",5);
                break;
            case R.id.level6button:
                i.putExtra("level",6);
                break;
            case R.id.level7button:
                i.putExtra("level",7);
                break;
            case R.id.level8button:
                i.putExtra("level",8);
                break;
            case R.id.level9button:
                i.putExtra("level",9);
                break;
            case R.id.level10button:
                i.putExtra("level",10);
                break;
            case R.id.level11button:
                i.putExtra("level",11);
                break;
            case R.id.level12button:
                i.putExtra("level",12);
                break;
            case R.id.level13button:
                i.putExtra("level",13);
                break;
            case R.id.level14button:
                i.putExtra("level",14);
                break;
            case R.id.level15button:
                i.putExtra("level",15);
                break;
            case R.id.level16button:
                i.putExtra("level",16);
                break;
            case R.id.level17button:
                i.putExtra("level",17);
                break;
            case R.id.level18button:
                i.putExtra("level",18);
                break;
            case R.id.level19button:
                i.putExtra("level",19);
                break;
            case R.id.level20button:
                i.putExtra("level",20);
                break;

            //Sign out button handles the user sign out.
            case R.id.signOutButton:
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Games.signOut(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                }
                i = new Intent(this, Main.class);
                break;
        }

        // Game activity is only launched when the startGame boolean is set
        if(startGame == true) {
            this.startActivity(i);
            this.finish();
        }
    }

    /*
    * linkUIElements pairs all of the references with the appropriate GUI element
     */
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

    /*
    * setClickListeners sets the Click Listener for all of the GUI buttons
     */
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

    /*
    * disableLevelButtons disables all of the level buttons.
     */
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

    /*
    * enableLevelButtons takes in the user's current level, and sets up the levels screen appropriately.
    * Finished levels will be green (or gold for Golden Cup levels), locked levels will be light gray,
    * and the current level is dark gray.
     */
    public void enableLevelButtons() {
        switch (level) {
            case 21:
                level20.setImageResource(R.drawable.goldcupconfetti);
            case 20:
                level20.setEnabled(true);
                if(level == 20)
                    level20.setImageResource(R.drawable.coffeecupdkgray);
                level19.setImageResource(R.drawable.coffeecupgreen);
            case 19:
                level19.setEnabled(true);
                if(level == 19)
                    level19.setImageResource(R.drawable.coffeecupdkgray);
                level18.setImageResource(R.drawable.goldcup);
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
                level14.setImageResource(R.drawable.goldcup);
            case 14:
                level14.setEnabled(true);
                if(level == 14)
                    level14.setImageResource(R.drawable.coffeecupdkgray);
                level13.setImageResource(R.drawable.goldcup);
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
                level11.setImageResource(R.drawable.goldcup);
            case 11:
                level11.setEnabled(true);
                if(level == 11)
                    level11.setImageResource(R.drawable.coffeecupdkgray);
                level10.setImageResource(R.drawable.goldcup);
            case 10:
                level10.setEnabled(true);
                if(level == 10)
                    level10.setImageResource(R.drawable.coffeecupdkgray);
                level9.setImageResource(R.drawable.coffeecupgreen);
            case 9:
                level9.setEnabled(true);
                if(level == 9)
                    level9.setImageResource(R.drawable.coffeecupdkgray);
                level8.setImageResource(R.drawable.goldcup);
            case 8:
                level8.setEnabled(true);
                if(level == 8)
                    level8.setImageResource(R.drawable.coffeecupdkgray);
                level7.setImageResource(R.drawable.coffeecupgreen);
            case 7:
                level7.setEnabled(true);
                if(level == 7)
                    level7.setImageResource(R.drawable.coffeecupdkgray);
                level6.setImageResource(R.drawable.goldcup);
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

    /*
    * startMediaPlayer handles the creation of the media player and begins audio playback.
     */
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

    /*
    * loadSavedInfo retrieves the user's level information from the game file.
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
    * updateUI is used to display or hide the sign out button if the user is signed in/out of Google
     */
    @Override
    protected void updateUI() {
        // Show signed in or signed out view
        if (isSignedIn() && level > 0) {

            findViewById(R.id.signOutButton).setVisibility(View.VISIBLE);
            unlockAchievement();
        } else {
            findViewById(R.id.signOutButton).setVisibility(View.INVISIBLE);
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
        this.finish();
    }

    /*
    * unlockAchievements is used to unlock all of the achievements for the levels the user has completed.
    * This ensures that the user's progress is accurate on Google Play if they completed levels while
    * signed out.
     */
    private void unlockAchievement() {
        switch(level - 1) {
            case 20:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQFg");
            case 19:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQFQ");
            case 18:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQFA");
            case 17:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQEw");
            case 16:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQEg");
            case 15:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQEQ");
            case 14:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQEA");
            case 13:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQDw");
            case 12:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQDg");
            case 11:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQDQ");
            case 10:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQDA");
            case 9:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQCw");
            case 8:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQCg");
            case 7:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQCQ");
            case 6:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQCA");
            case 5:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQBQ");
            case 4:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQBA");
            case 3:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQAw");
            case 2:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQAg");
            case 1:
                Games.Achievements.unlock(mGoogleApiClient,"CgkIto6VvK4ZEAIQAQ");
                break;
        }
    }

}

