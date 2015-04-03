package edu.oakland.ultimatebarista;

//Imports allow usage of built-in functionality
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.games.Games;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.Timer;

//Class which controls Game-Screen functionality
public class Game extends GoogleAPI implements View.OnClickListener{
    /*
    * Lots of GUI elements to create references for
     */

    //MediaPlayer for music PlayBack
    private MediaPlayer mp, mpAlert = null;

    //ImageButtons for the various game buttons
    private ImageButton handButton,trashButton,smallButton,medButton,largeButton,wholeButton,
            nonfatButton,soyButton,espButton,dcfButton,steamButton,caramelButton,
            hazelnutButton,vanillaButton,toffeeButton,peppermintButton,chocolateButton,
            whipCreamButton = null;

    //Vibrator for button feedback
    private Vibrator vibrator = null;

    //ProgressBar for showing User's level progress
    private ProgressBar levelProgress = null;

    //TextViews to display text elements throughout the game
    private TextView levelProgressText,levelTimeRemaining,levelFinishedText,levelInfoTitle,
            levelInfoSubtitle,levelInfoObjective,numOfDecafText,numOfRegText,levelTitle,
            drinkDisplay, handFeedbackText, errorText = null;

    private ImageView confetti = null;


    //Regular buttons to control menu flow
    private Button nextLevelButton,levelInfoBegin, levelSelectionButton = null;

    //Layouts containing all of the buttons
    private RelativeLayout levelCompletionLayout,levelInfoLayout,gameLayout, errorBox = null;

    //Number of drinks made by user
    private int drinksMade = 0;
    private int incorrectDrinks = 0;

    //current level, highest level user has completed
    private int level = 0;
    private int maxLevelCompleted = 0;


    //All of the level information, title, subtitle, drinks required, and time limit
    private String titleText = null;
    private String levelText = null;
    private String subtitleText = null;
    private long timeLimit = 0;
    private int drinksGoal = 0;

    //CountDownTimer is used to track level time limits
    CountDownTimer countDownTimer = null;

    //Constant Integers to make array referencing clearer
    private final int CUP_SIZE = 0;
    private final int STEAM = 1;
    private final int MILK_TYPE = 2;
    private final int TOFFEE_SHOT = 3;
    private  final int CHOC_SHOT = 4;
    private final int PEP_SHOT = 5;
    private final int HAZEL_SHOT = 6;
    private final int CARA_SHOT = 7;
    private final int VAN_SHOT = 8;
    private final int DEC_ESP = 9;
    private final int REG_ESP = 10;
    private final int WHP_CRM = 11;

    //Three arrays to hold: Customer Drink, User Drink, and the empty array to help reinitialize user drink
     /*
    *   Drink Example
    *
    *   Int[] Drink = {CUP_SIZE, STEAM, MILK_TYPE, TOFFEE_SHOT, CHOC_SHOT, PEP_SHOT, HAZEL_SHOT, CARA_SHOT, VAN_SHOT, DEC_ESP, REG_ESP, WHP_CRM}
    *   Where   CUP_SIZE is 0: small, 1: medium, 2: large
    *       MILK_TYPE is 0: whole, 1: Nonfat, 2: Soy
    *       STEAM is 0: none, 1: steamed
    *       TOFFEE_SHOT is 0: none, 1: added
    *       ---same for evey syrup shot---
    *       Chocolate, Peppermint, Hazelnut, Caramel, Vanilla
    *       DEC_ESP is x: where x is the amount of shots added
    *       REG_ESP is y: where y is the amount of shots added
    *       WHP_CRM is 0: none, 1: added
     */
    private int[] customerOrder = new int[12];
    private int[] userBeverage = {-1,0,-1,0,0,0,0,0,0,0,0,0};
    protected final int[] emptyArray = {-1,0,-1,0,0,0,0,0,0,0,0,0};
    private int[] previousBeverage = new int[12];

    private String fileName = "ultimatebarista.txt", finishedText = null;

    private int mediaFile = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        linkUIElements();
        setClickListeners();
        retrieveLevelInfo();

        //Check User's level to see if it is the tutorial level
        if(level > 0) {
            setUpLevel();
            displayObjectives();
        } else {
            runTutorial();
        }
    }

    /*
    * The runTutorial method is used to set up the level for the tutorial
     */
    private void runTutorial() {
        //Fills in the level's TextViews with appropriate tutorial level text
        levelInfoTitle.setText(getString(R.string.tutorialLevelTitle));
        levelInfoObjective.setText(getString(R.string.tutorialLevelObjective));
        titleText = getString(R.string.tutorialLevelTitle);
        levelTitle.setText(getString(R.string.tutorialLevelTitle));
        drinksGoal = Integer.valueOf(getString(R.string.tutorialdrinks));
        finishedText = getString(R.string.tutorialfinishtext);

        //Makes the information screen visible, and hides the progress bar/countdown for this tutorial
        //level
        levelInfoLayout.setVisibility(View.VISIBLE);
        levelTimeRemaining.setVisibility(View.INVISIBLE);

        //Sets up tutorial audio file, level progress bar, and sets background
        mediaFile = R.raw.progressivehouse;
        levelProgress.setMax(drinksGoal);
        gameLayout.setBackgroundResource(R.drawable.bg);

        //Generates and displays a random drink
        randomDrinkGen();
        displayDrinkOrder();
    }

    /*
    * displayObjectives is used to display the level's objectives to the user before the game begins
     */
    private void displayObjectives() {
        levelInfoTitle.setText(titleText);
        if (subtitleText != null) {
            levelInfoSubtitle.setText(subtitleText);
        }
        levelInfoBegin.setText("Continue");
        levelInfoObjective.setText(levelText);
        levelInfoLayout.setVisibility(View.VISIBLE);
    }

    /*
    * retrieveLevelInfo retrieves and stores the game level, and max level completed by the player.
     */
    private void retrieveLevelInfo() {
        Intent i = getIntent();
        level = i.getIntExtra("level",-1);
        maxLevelCompleted = i.getIntExtra("maxLevelCompleted",0);
    }

    /*
    * setUpLevel sets up the level by updating the textviews, progress bar, and timer with the
    * information for the current level. Also sets the audio file and level background.
    * Level information can be viewed in strings.xml
     */
    private void setUpLevel() {
        setGamePlayButtonsEnabled(false);
        switch(level) {
            case 20:
                gameLayout.setBackgroundResource(R.drawable.bgfrance);
                levelText = getString(R.string.level20text);
                titleText = getString(R.string.level20title);
                subtitleText = getString(R.string.level20subtitle);
                drinksGoal = Integer.valueOf(getString(R.string.level20drinks));
                timeLimit = Long.valueOf(getString(R.string.level20time)) * 1000;
                finishedText = getString(R.string.level20finishtext);
                mediaFile = R.raw.dogma;
                break;
            case 19:
                gameLayout.setBackgroundResource(R.drawable.bgfrance);
                levelText = getString(R.string.level19text);
                titleText = getString(R.string.level19title);
                subtitleText = getString(R.string.level19subtitle);
                drinksGoal = Integer.valueOf(getString(R.string.level19drinks));
                timeLimit = Long.valueOf(getString(R.string.level19time)) * 1000;
                finishedText = getString(R.string.level19finishtext);
                mediaFile = R.raw.travel;
                break;
            case 18:
                gameLayout.setBackgroundResource(R.drawable.bgscotland);
                levelText = getString(R.string.level18text);
                titleText = getString(R.string.level18title);
                subtitleText = getString(R.string.level18subtitle);
                drinksGoal = Integer.valueOf(getString(R.string.level18drinks));
                timeLimit = Long.valueOf(getString(R.string.level18time)) * 1000;
                finishedText = getString(R.string.level18finishtext);
                mediaFile = R.raw.shetland2;
                break;
            case 17:
                gameLayout.setBackgroundResource(R.drawable.bgscotland);
                levelText = getString(R.string.level17text);
                titleText = getString(R.string.level17title);
                subtitleText = getString(R.string.level17subtitle);
                drinksGoal = Integer.valueOf(getString(R.string.level17drinks));
                timeLimit = Long.valueOf(getString(R.string.level17time)) * 1000;
                finishedText = getString(R.string.level17finishtext);
                mediaFile = R.raw.shetland;
                break;
            case 16:
                gameLayout.setBackgroundResource(R.drawable.bgitaly);
                levelText = getString(R.string.level16text);
                titleText = getString(R.string.level16title);
                subtitleText = getString(R.string.level16subtitle);
                drinksGoal = Integer.valueOf(getString(R.string.level16drinks));
                timeLimit = Long.valueOf(getString(R.string.level16time)) * 1000;
                finishedText = getString(R.string.level16finishtext);
                mediaFile = R.raw.dolcevita;
                break;
            case 15:
                gameLayout.setBackgroundResource(R.drawable.bgitaly);
                levelText = getString(R.string.level15text);
                titleText = getString(R.string.level15title);
                subtitleText = getString(R.string.level15subtitle);
                drinksGoal = Integer.valueOf(getString(R.string.level15drinks));
                timeLimit = Long.valueOf(getString(R.string.level15time)) * 1000;
                finishedText = getString(R.string.level15finishtext);
                mediaFile = R.raw.vino;
                break;
            case 14:
                gameLayout.setBackgroundResource(R.drawable.egyptbackground);
                levelText = getString(R.string.level14text);
                titleText = getString(R.string.level14title);
                subtitleText = getString(R.string.level14subtitle);
                drinksGoal = Integer.valueOf(getString(R.string.level14drinks));
                timeLimit = Long.valueOf(getString(R.string.level14time)) * 1000;
                finishedText = getString(R.string.level14finishtext);
                mediaFile = R.raw.tigris;
                break;
            case 13:
                gameLayout.setBackgroundResource(R.drawable.bgindia);
                levelText = getString(R.string.level13text);
                titleText = getString(R.string.level13title);
                subtitleText = getString(R.string.level13subtitle);
                drinksGoal = Integer.valueOf(getString(R.string.level13drinks));
                timeLimit = Long.valueOf(getString(R.string.level13time)) * 1000;
                finishedText = getString(R.string.level13finishtext);
                mediaFile = R.raw.celestialbody;
                break;
            case 12:
                gameLayout.setBackgroundResource(R.drawable.bgchina);
                levelText = getString(R.string.level12text);
                titleText = getString(R.string.level12title);
                subtitleText = getString(R.string.level12subtitle);
                drinksGoal = Integer.valueOf(getString(R.string.level12drinks));
                timeLimit = Long.valueOf(getString(R.string.level12time)) * 1000;
                finishedText = getString(R.string.level12finishtext);
                mediaFile = R.raw.shogun;
                break;
            case 11:
                gameLayout.setBackgroundResource(R.drawable.bgbrazil);
                levelText = getString(R.string.level11text);
                titleText = getString(R.string.level11title);
                subtitleText = getString(R.string.level11subtitle);
                drinksGoal = Integer.valueOf(getString(R.string.level11drinks));
                timeLimit = Long.valueOf(getString(R.string.level11time)) * 1000;
                finishedText = getString(R.string.level11finishtext);
                mediaFile = R.raw.island;
                break;
            case 10:
                gameLayout.setBackgroundResource(R.drawable.bgmexico);
                levelText = getString(R.string.level10text);
                titleText = getString(R.string.level10title);
                subtitleText = getString(R.string.level10subtitle);
                drinksGoal = Integer.valueOf(getString(R.string.level10drinks));
                timeLimit = Long.valueOf(getString(R.string.level10time)) * 1000;
                finishedText = getString(R.string.level10finishtext);
                mediaFile = R.raw.havana;
                break;
            case 9:
                gameLayout.setBackgroundResource(R.drawable.bg);
                levelText = getString(R.string.level9text);
                titleText = getString(R.string.level9title);
                subtitleText = getString(R.string.trainingtitle);
                drinksGoal = Integer.valueOf(getString(R.string.trainingdrinks));
                timeLimit = Long.valueOf(getString(R.string.trainingtime)) * 1000;
                finishedText = getString(R.string.level9finishtext);
                mediaFile = R.raw.progressivehouse;
                break;
            case 8:
                gameLayout.setBackgroundResource(R.drawable.bghollywood);
                levelText = getString(R.string.level8text);
                titleText = getString(R.string.level8title);
                subtitleText = getString(R.string.level8subtitle);
                drinksGoal = Integer.valueOf(getString(R.string.level8drinks));
                timeLimit = Long.valueOf(getString(R.string.level8time)) * 1000;
                finishedText = getString(R.string.level8finishtext);
                mediaFile = R.raw.catwalk;
                break;
            case 7:
                gameLayout.setBackgroundResource(R.drawable.bg);
                levelText = getString(R.string.level7text);
                titleText = getString(R.string.level7title);
                subtitleText = getString(R.string.trainingtitle);
                drinksGoal = Integer.valueOf(getString(R.string.trainingdrinks));
                timeLimit = Long.valueOf(getString(R.string.trainingtime)) * 1000;
                finishedText = getString(R.string.level7finishtext);
                mediaFile = R.raw.progressivehouse;
                break;
            case 6:
                gameLayout.setBackgroundResource(R.drawable.bgnewyork);
                levelText = getString(R.string.level6text);
                titleText = getString(R.string.level6title);
                subtitleText = getString(R.string.level6subtitle);
                drinksGoal = Integer.valueOf(getString(R.string.level6drinks));
                timeLimit = Long.valueOf(getString(R.string.level6time)) * 1000;
                finishedText = getString(R.string.level6finishtext);
                mediaFile = R.raw.bossaloungerlong;
                break;
            case 5:
                gameLayout.setBackgroundResource(R.drawable.bgnewyork);
                levelText = getString(R.string.level5text);
                titleText = getString(R.string.level5title);
                subtitleText = getString(R.string.level5subtitle);
                drinksGoal = Integer.valueOf(getString(R.string.level5drinks));
                timeLimit = Long.valueOf(getString(R.string.level5time)) * 1000;
                finishedText = getString(R.string.level5finishtext);
                mediaFile = R.raw.parkbench;
                break;
            case 4:
                gameLayout.setBackgroundResource(R.drawable.bg);
                levelText = getString(R.string.level4text);
                titleText = getString(R.string.level4title);
                subtitleText = getString(R.string.trainingtitle);
                drinksGoal = Integer.valueOf(getString(R.string.trainingdrinks));
                timeLimit = Long.valueOf(getString(R.string.trainingtime)) * 1000;
                finishedText = getString(R.string.level4finishtext);
                mediaFile = R.raw.progressivehouse;
                break;
            case 3:
                gameLayout.setBackgroundResource(R.drawable.bg);
                levelText = getString(R.string.level3text);
                titleText = getString(R.string.level3title);
                subtitleText = getString(R.string.trainingtitle);
                drinksGoal = Integer.valueOf(getString(R.string.trainingdrinks));
                timeLimit = Long.valueOf(getString(R.string.trainingtime)) * 1000;
                finishedText = getString(R.string.level3finishtext);
                mediaFile = R.raw.progressivehouse;
                break;
            case 2:
                gameLayout.setBackgroundResource(R.drawable.bg);
                levelText = getString(R.string.level2text);
                titleText = getString(R.string.level2title);
                subtitleText = getString(R.string.trainingtitle);
                drinksGoal = Integer.valueOf(getString(R.string.trainingdrinks));
                timeLimit = Long.valueOf(getString(R.string.trainingtime)) * 1000;
                finishedText = getString(R.string.level2finishtext);
                mediaFile = R.raw.progressivehouse;
                break;
            case 1:
                gameLayout.setBackgroundResource(R.drawable.bg);
                levelText = getString(R.string.level1text);
                titleText = getString(R.string.level1title);
                subtitleText = getString(R.string.trainingtitle);
                drinksGoal = Integer.valueOf(getString(R.string.trainingdrinks));
                timeLimit = Long.valueOf(getString(R.string.trainingtime)) * 1000;
                finishedText = getString(R.string.level1finishtext);
                mediaFile = R.raw.progressivehouse;
                break;
        }

        //All of the competition levels display the City Name as the level title, rather than the
        //competition name
        if(level > 3 && level != 7 && level != 9) {
            levelTitle.setText(subtitleText);
        } else {
            levelTitle.setText(titleText);
        }

        //Sets up the level progress bar with the goals
        levelProgress.setMax(drinksGoal);
        levelProgress.setVisibility(View.VISIBLE);
        levelProgressText.setVisibility(View.VISIBLE);
        levelProgressText.setText(drinksMade + "/" + drinksGoal);


        //Begins the countdown timer
        countDownTimer = new CountDownTimer(timeLimit, 500) {
            boolean songPlaying = false;
            @Override
            public void onTick(long millisUntilFinished) {
                levelTimeRemaining.setText(String.valueOf(millisUntilFinished / 1000));
                if(millisUntilFinished <= 3500 && !songPlaying) {
                    mpAlert = MediaPlayer.create(getBaseContext(), R.raw.alarm);
                    mpAlert.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mpAlert.start();
                        }
                    });
                    songPlaying = true;
                }
            }

            //If time runs out, level is failed.
            @Override
            public void onFinish() {
                levelFinished(false);
            }
        };

    }

    /*
    * beginLevel begins the level by generating a drink order, displaying the drink order, and beginning the countdown timer if the level requires it.
     */
    private void beginLevel() {
        gameLayout.setAlpha(1.0f);
        levelInfoLayout.setVisibility(View.INVISIBLE);
        randomDrinkGen();
        displayDrinkOrder();

        if(level > 0) {
            countDownTimer.start();
        }

    }

    /*
    * startMediaPlayer loads the audio file and begins playback
     */
    public void startMediaPlayer() {
        mp = MediaPlayer.create(this, mediaFile);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.start();
            }
        });
    }

    /*
    * randomDrinkGen Generates a random drink based on the current level
     */
    protected void randomDrinkGen() {
        int[] drink = new int[12];
        Random rand = new Random();
        switch(level) {
            //Levels 10-20 do not have any added features
            case 20:
            case 19:
            case 18:
            case 17:
            case 16:
            case 15:
            case 14:
            case 13:
            case 12:
            case 11:
            case 10:
            case 9: //levels 9 and above add the bottom row of syrups
                int syrup = rand.nextInt(4);
                switch(syrup) {
                    case 1:
                        drink[PEP_SHOT] = 1;
                        break;
                    case 2:
                        drink[TOFFEE_SHOT] = 1;
                        drink[CHOC_SHOT] = 1;
                        break;
                    case 3:
                        drink[TOFFEE_SHOT] = 1;
                        drink[CHOC_SHOT] = 1;
                        drink[PEP_SHOT] = 1;
                        break;
                }
            case 8: //level 8 has no added features
            case 7: //levels 7 and above add the top row of syrups
                syrup = rand.nextInt(3);
                switch(syrup){
                    case 0:
                        drink[HAZEL_SHOT] = 1;
                        break;
                    case 1:
                        drink[CARA_SHOT] = 1;
                        drink[VAN_SHOT] = 1;
                        break;
                    case 2:
                        drink[CARA_SHOT] = 1;
                        drink[HAZEL_SHOT] = 1;
                        drink[VAN_SHOT] = 1;
                        break;
                }
            case 6: //level 5 & 6 have no added features
            case 5:
            case 4://levels 4 and above add the possibility for 0-4 decaf shots of espresso
                if(level == 4) {
                    drink[DEC_ESP] = 1;
                } else {
                    drink[DEC_ESP] = rand.nextInt(5);
                }
            case 3: //levels 3 and above add the possibility for 0-4 regular shots of espresso
                if(level == 3) {
                    drink[REG_ESP] = 1;
                } else {
                    drink[REG_ESP] = rand.nextInt(5);
                }
            case 2: //levels 2 and above add the possibility for whip cream
                if(level == 2) {
                    drink[WHP_CRM] = 1;
                } else {
                    drink[WHP_CRM] = rand.nextInt(2);
                }
            case 1: //level 1 has no added feature
            case 0: //levels 0 and above require a cup size, possibility for steaming, and require
                    //a milk type
                drink[CUP_SIZE] = rand.nextInt(3);
                drink[STEAM] = rand.nextInt(2);
                drink[MILK_TYPE] = rand.nextInt(3);
                break;

        }
        //sets the customer order to the generated drink
        customerOrder = drink.clone();

    }

    /*
    * displayDrinkOrder displays the information from the customerOrder array into the TextView
    * on the screen
     */
    public void displayDrinkOrder() {
        StringBuffer drinkOrder = new StringBuffer();
        switch(customerOrder[CUP_SIZE]) {
            case 0:
                drinkOrder.append(getString(R.string.small) + "\n");
                break;
            case 1:
                drinkOrder.append(getString(R.string.medium) + "\n");
                break;
            case 2:
                drinkOrder.append(getString(R.string.large) + "\n");
                break;
        }
        switch(customerOrder[MILK_TYPE]) {
            case 0:
                if(customerOrder[STEAM] == 1)  {
                    drinkOrder.append(getString(R.string.whole) + " Steamed \n");
                } else {
                    drinkOrder.append(getString(R.string.whole) + "\n");
                }
                break;
            case 1:
                if(customerOrder[STEAM] == 1)  {
                    drinkOrder.append(getString(R.string.nonfat) + " Steamed \n");
                } else {
                    drinkOrder.append(getString(R.string.nonfat) + "\n");
                }
                break;
            case 2:
                if(customerOrder[STEAM] == 1)  {
                    drinkOrder.append(getString(R.string.soy) + " Steamed \n");
                } else {
                    drinkOrder.append(getString(R.string.soy) + "\n");
                }
                break;
        }
        if(customerOrder[DEC_ESP] > 0) {
            drinkOrder.append(Integer.toString(customerOrder[9]) + " " +getString(R.string.decaf) + "\n");
        }
        if (customerOrder[REG_ESP] > 0 ){
            drinkOrder.append(Integer.toString(customerOrder[10]) + " " + getString(R.string.regular) + "\n");
        }
        if((customerOrder[CARA_SHOT] == 1) && (customerOrder[HAZEL_SHOT] == 0) &&
            (customerOrder[VAN_SHOT] == 0) && (customerOrder[TOFFEE_SHOT] == 0) &&
            (customerOrder[PEP_SHOT] == 0) && (customerOrder[CHOC_SHOT] == 0))
            drinkOrder.append(getString(R.string.caramel) + "\n");
        else if(customerOrder[CARA_SHOT] == 1){
            drinkOrder.append(getString(R.string.caramel) + "     ");
        }
        if((customerOrder[HAZEL_SHOT] == 1) && (customerOrder[VAN_SHOT] == 0) &&
                (customerOrder[TOFFEE_SHOT] == 0) && (customerOrder[PEP_SHOT] == 0) &&
                (customerOrder[CHOC_SHOT] == 0)) {
            drinkOrder.append(getString(R.string.hazelnut) +"\n");
        } else if((customerOrder[HAZEL_SHOT] == 1)) {
            drinkOrder.append(getString(R.string.hazelnut) +"     ");
        }
        if((customerOrder[VAN_SHOT] == 1) &&
        (customerOrder[TOFFEE_SHOT] == 0) && (customerOrder[PEP_SHOT] == 0) &&
                (customerOrder[CHOC_SHOT] == 0)) {
            drinkOrder.append(getString(R.string.vanilla) + "\n");
        } else if (customerOrder[VAN_SHOT] == 1) {
            drinkOrder.append(getString(R.string.vanilla) + "    ");
        }
        if((customerOrder[TOFFEE_SHOT] == 1) && (customerOrder[PEP_SHOT] == 0) &&
                (customerOrder[CHOC_SHOT] == 0)) {
            drinkOrder.append(getString(R.string.toffee) + "\n");
        }
        else if(customerOrder[TOFFEE_SHOT] == 1) {
            drinkOrder.append(getString(R.string.toffee) + "      ");
        }
        if ((customerOrder[PEP_SHOT] == 1) &&
            (customerOrder[CHOC_SHOT] == 0)) {
            drinkOrder.append(getString(R.string.peppermint) + "\n");
        } else if(customerOrder[PEP_SHOT] == 1) {
            drinkOrder.append(getString(R.string.peppermint) + "      ");
        }
        if(customerOrder[CHOC_SHOT] == 1) drinkOrder.append(getString(R.string.chocolate) + "\n");
        if(customerOrder[WHP_CRM] == 1) drinkOrder.append(getString(R.string.whip));

        //updates the TextView to display the customer's order using the generated String
        drinkDisplay.setText(drinkOrder.toString());
    }


    /*
    * compareDrinks loops through the customerOrder and userBeverage arrays and returns true if all
    * attributes are the same
     */
    public boolean compareDrinks() {
        int i = 0;

        while(i < customerOrder.length) {
            if(customerOrder[i] != userBeverage [i]) {
                return false;
            }
            i++;
        }
        return true;
    }

    /*
    * updateLevelProgress Updates the progress bar for the level, if the level goals are satisfied
    * the countDownTimer is stopped and levelFinished method is called
     */
    public void updateLevelProgress() {
        levelProgress.setProgress(drinksMade);
        levelProgressText.setText(drinksMade + "/" + drinksGoal);
        if(drinksMade == drinksGoal) {
            if(level > 0) {
                countDownTimer.cancel();
            }
            levelFinished(true);

        }
    }

    /*
    * levelFinished is used to display a message to the user that lets them know they finished the
    * level objectives, or ran out of time to complete the level. It also calls the method that updates
    * the save file if the user has increased their maximum completed level.
     */
    public void levelFinished(boolean metObjectives) {

        //The level completion box is made visible, and the game play screen is dimmed
        levelCompletionLayout.setVisibility(View.VISIBLE);
        gameLayout.setAlpha(0.3f);
        setGamePlayButtonsEnabled(false);
        levelProgress.setVisibility(View.INVISIBLE);
        levelProgressText.setVisibility(View.INVISIBLE);
        levelTimeRemaining.setText("");
        drinkDisplay.setText("");

        //True if user completed all goals, false if user failed level
        if(metObjectives) {
            levelCompletionLayout.setBackgroundColor(Color.parseColor("#ff1daf13"));
            levelFinishedText.setText(finishedText);

            if (isSignedIn()) {
                unlockAchievement();
            }
            level++;

            //Save file is only updated if the level is greater than the max completed level
            if(level > maxLevelCompleted) {
                updateSaveFile();
            }
            if(level > 20) {
                nextLevelButton.setVisibility(View.GONE);
                levelSelectionButton.setWidth(1000);
                mp.release();
                mediaFile = R.raw.gamecompleted;

                findViewById(R.id.confetti).setVisibility(View.VISIBLE);
                findViewById(R.id.confetti2).setVisibility(View.VISIBLE);
                /*
                Animation a = new RotateAnimation(0.0f, 360.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);
                a.setRepeatCount(500);
                a.setDuration(10);
                */
                TranslateAnimation animation = new TranslateAnimation(-100.0f, 100f,
                        0f,0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
                animation.setDuration(1000);  // animation duration
                animation.setRepeatCount(500);  // animation repeat count
                animation.setRepeatMode(2);   // repeat animation (left to right, right to left )

                TranslateAnimation animation2 = new TranslateAnimation(100.0f, -100f,
                        0f,0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
                animation2.setDuration(1000);  // animation duration
                animation2.setRepeatCount(500);  // animation repeat count
                animation2.setRepeatMode(2);   // repeat animation (left to right, right to left )

                findViewById(R.id.confetti).startAnimation(animation);
                findViewById(R.id.confetti2).startAnimation(animation2);
                startMediaPlayer();
            }
            nextLevelButton.setText("Next Level");

        } else if (incorrectDrinks == 3) {
            levelCompletionLayout.setBackgroundColor(Color.parseColor("#ffca1a24"));
            levelFinishedText.setText(getString(R.string.tooManyIncorrect));
            nextLevelButton.setText("Retry Level");
        } else {
            levelCompletionLayout.setBackgroundColor(Color.parseColor("#ffca1a24"));
            levelFinishedText.setText(getString(R.string.levelFailedText));
            nextLevelButton.setText("Retry Level");
            clearSelected();
            userBeverage = emptyArray.clone();
        }

    }


    /*
    * unlockAchievement is called to unlock Google Play Game Services achievements when the player
    * has completed certain milestones in the game.
     */
    private void unlockAchievement() {
        switch(level) {
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

    /*
    * updateSaveFile updates the game's save file to store the user's current level.
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

    /*
    * clearSelected Removes the highlight from all buttons to let user know that their beverage
    * has been reset
     */
    public void clearSelected() {
        smallButton.clearColorFilter();
        medButton.clearColorFilter();
        largeButton.clearColorFilter();

        nonfatButton.clearColorFilter();
        wholeButton.clearColorFilter();
        soyButton.clearColorFilter();
        whipCreamButton.clearColorFilter();

        caramelButton.clearColorFilter();
        vanillaButton.clearColorFilter();
        hazelnutButton.clearColorFilter();
        toffeeButton.clearColorFilter();
        chocolateButton.clearColorFilter();
        peppermintButton.clearColorFilter();

        steamButton.clearColorFilter();
        dcfButton.clearColorFilter();
        espButton.clearColorFilter();

        numOfDecafText.setText("");
        numOfRegText.setText("");
    }

    /*
    * linkUIElements links all of the GUI assets with the corresponding variable
     */
    public void linkUIElements() {

        levelFinishedText = (TextView) findViewById(R.id.levelFinishedText);
        drinkDisplay = (TextView) findViewById(R.id.drinkDisplay);

        levelCompletionLayout = (RelativeLayout) findViewById(R.id.levelCompletionLayout);
        nextLevelButton = (Button) findViewById(R.id.nextLevelButton);

        levelTimeRemaining = (TextView) findViewById(R.id.levelTimeRemaining);

        levelTitle = (TextView) findViewById(R.id.levelTitle);
        handButton = (ImageButton) findViewById(R.id.handButton);
        trashButton = (ImageButton) findViewById(R.id.trashButton);

        smallButton = (ImageButton) findViewById(R.id.smallButton);
        medButton = (ImageButton) findViewById(R.id.medButton);
        largeButton = (ImageButton) findViewById(R.id.largeButton);

        nonfatButton = (ImageButton) findViewById(R.id.nonfatButton);
        wholeButton = (ImageButton) findViewById(R.id.wholeButton);
        soyButton = (ImageButton) findViewById(R.id.soyButton);

        steamButton = (ImageButton) findViewById(R.id.steamButton);

        espButton = (ImageButton) findViewById(R.id.espButton);
        dcfButton = (ImageButton) findViewById(R.id.dcfButton);

        vanillaButton = (ImageButton) findViewById(R.id.vanillaButton);
        hazelnutButton = (ImageButton) findViewById(R.id.hazelnutButton);
        chocolateButton = (ImageButton) findViewById(R.id.chocolateButton);
        toffeeButton = (ImageButton) findViewById(R.id.toffeeButton);
        peppermintButton = (ImageButton) findViewById(R.id.peppermintButton);
        caramelButton = (ImageButton) findViewById(R.id.caramelButton);
        whipCreamButton = (ImageButton) findViewById(R.id.whipButton);
        levelProgress = (ProgressBar) findViewById(R.id.levelProgressBar);

        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        levelProgressText = (TextView) findViewById(R.id.levelProgressText);

        numOfDecafText = (TextView) findViewById(R.id.numOfDecafText);
        numOfRegText = (TextView) findViewById(R.id.numOfRegText);

        levelInfoBegin = (Button) findViewById(R.id.levelInfoBegin);
        levelInfoTitle = (TextView) findViewById(R.id.levelInfoTitle);
        levelInfoSubtitle = (TextView) findViewById(R.id.levelInfoSubtitle);
        levelInfoObjective = (TextView) findViewById(R.id.levelInfoObjective);
        levelInfoLayout = (RelativeLayout) findViewById(R.id.levelInfoLayout);
        gameLayout = (RelativeLayout) findViewById(R.id.gameLayout);

        levelSelectionButton = (Button) findViewById(R.id.levelSelectionButton);

        handFeedbackText = (TextView) findViewById(R.id.handFeedbackText);
        errorBox = (RelativeLayout) findViewById(R.id.errorBox);
        errorText = (TextView) findViewById(R.id.errorText);
    }

    /*
    * setClickListeners links all of the GUI's buttons with the click listeners
     */
    public void setClickListeners() {

        nextLevelButton.setOnClickListener(this);

        handButton.setOnClickListener(this);
        trashButton.setOnClickListener(this);

        smallButton.setOnClickListener(this);
        medButton.setOnClickListener(this);
        largeButton.setOnClickListener(this);

        nonfatButton.setOnClickListener(this);
        wholeButton.setOnClickListener(this);
        soyButton.setOnClickListener(this);

        steamButton.setOnClickListener(this);

        espButton.setOnClickListener(this);
        dcfButton.setOnClickListener(this);

        vanillaButton.setOnClickListener(this);
        hazelnutButton.setOnClickListener(this);
        chocolateButton.setOnClickListener(this);
        toffeeButton.setOnClickListener(this);
        peppermintButton.setOnClickListener(this);
        caramelButton.setOnClickListener(this);
        whipCreamButton.setOnClickListener(this);

        levelInfoBegin.setOnClickListener(this);
        levelSelectionButton.setOnClickListener(this);


    }



    /*
    Class Listener, takes all button input and switches cases based on which button threw the event
    HandButton: Checks if player drink is equal to customer drink; if yes, succeed, if no, fail
    TrashButton: Sets the player drink equal to the empty array, allowing them to start over
    Small/Med/Large Button: Allows user to grab a cup of that size
    All other buttons add something to the drink
    */
    @Override
    public void onClick(View v) {
        vibrator.vibrate(25);
        levelProgressText.setText(drinksMade + "/" + drinksGoal);
        switch (v.getId()) {
            case R.id.levelSelectionButton:
                Intent i = new Intent(this, Levels.class);
                startActivity(i);
                this.finish();
                break;
            case R.id.nextLevelButton:
                if (nextLevelButton.getText() == "Retry Level") {
                    setGamePlayButtonsEnabled(true);
                    setUpLevel();
                    displayObjectives();
                    levelCompletionLayout.setVisibility(View.GONE);
                    levelProgress.setProgress(0);
                    drinksMade = 0;
                    incorrectDrinks = 0;
                } else {
                    setGamePlayButtonsEnabled(true);
                    setUpLevel();
                    displayObjectives();
                    if (mp != null) {
                        mp.release();
                    }
                    startMediaPlayer();
                    levelCompletionLayout.setVisibility(View.GONE);
                    drinksMade = 0;
                    incorrectDrinks = 0;
                    levelProgress.setProgress(0);
                }
                break;
            case R.id.levelInfoBegin:
                if (levelInfoBegin.getText() == "Begin Level") //Being level button is active
                {
                    beginLevel();
                    setGamePlayButtonsEnabled(true);
                } else {
                    StringBuffer levelObjective = new StringBuffer();
                    levelObjective.append("Goal: Make ");
                    levelObjective.append(drinksGoal);
                    if (timeLimit > 0) {
                        levelObjective.append(" drinks in ");
                        levelObjective.append(timeLimit / 1000);
                        levelObjective.append(" seconds.");
                    } else {
                        levelObjective.append(" drinks!");
                    }
                    levelInfoObjective.setText(levelObjective.toString());
                    levelInfoBegin.setText("Begin Level");
                }

                break;
            case R.id.handButton:
                clearSelected();
                //if drinks are the same, increase counter, update level progress, and generate new
                //drink order
                if (compareDrinks()) {
                    ;
                    drinksMade++;
                    previousBeverage = customerOrder.clone();
                    // generates a new drink
                    //If the milk type is the same, changes to a different type. This prevents
                    //the user from having the same drink twice.
                    randomDrinkGen();
                    if (customerOrder[MILK_TYPE] == previousBeverage[MILK_TYPE]) {
                        if (previousBeverage[MILK_TYPE] < 2) {
                            customerOrder[MILK_TYPE]++;
                        } else {
                            customerOrder[MILK_TYPE]--;
                        }
                    }
                    displayDrinkOrder();

                    updateLevelProgress();
                } else { //drink is incorrect, inform user
                    if (incorrectDrinks < 3) {
                        incorrectDrinks++;
                    }
                    vibrator.vibrate(400);

                    //CountDownTimer makes 1-3 X's appear for 500ms.
                    CountDownTimer c = new CountDownTimer(500, 100) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            handFeedbackText.setText("");
                            if (incorrectDrinks == 3) {
                                levelFinished(false);
                            }
                        }
                    };
                    switch (incorrectDrinks) {
                        case 1:
                            handFeedbackText.setText("X");
                            c.start();
                            break;
                        case 2:
                            handFeedbackText.setText("X X");
                            c.start();
                            break;
                        case 3:
                            handFeedbackText.setText("X X X");
                            countDownTimer.cancel();
                            c.start();
                            break;
                    }
                }
                //clear the user's created beverage
                userBeverage = emptyArray.clone();
                break;

            case R.id.trashButton:
                clearSelected();
                //clear user's created beverage, and inform user
                userBeverage = emptyArray.clone();
                displayErrorMessage(6);
                break;

            /*
            * The following buttons set the drink size for the user beverage, if size is already
            * selected an error message is generated
             */
            case R.id.smallButton:
                if (userBeverage[CUP_SIZE] >= 0) {
                    displayErrorMessage(2);
                } else {
                    userBeverage[CUP_SIZE] = 0;
                    smallButton.setColorFilter(Color.argb(100, 0, 77, 255));
                }
                break;
            case R.id.medButton:
                if (userBeverage[CUP_SIZE] >= 0) {
                    displayErrorMessage(2);
                } else {
                    userBeverage[CUP_SIZE] = 1;
                    medButton.setColorFilter(Color.argb(100, 0, 77, 255));
                }
                break;
            case R.id.largeButton:
                if (userBeverage[CUP_SIZE] >= 0) {
                    displayErrorMessage(2);
                } else {
                    userBeverage[CUP_SIZE] = 2;
                    largeButton.setColorFilter(Color.argb(100, 0, 77, 255));
                }
                break;

            /*
            * The following buttons select the milk type for the user beverage, if a milk type
            * was already selected an error is generated
             */
            case R.id.wholeButton:
                if (userBeverage[CUP_SIZE] < 0) {
                    displayErrorMessage(1);
                } else if (userBeverage[MILK_TYPE] >= 0) {
                    displayErrorMessage(4);
                } else {
                    userBeverage[MILK_TYPE] = 0;
                    wholeButton.setColorFilter(Color.argb(100, 0, 77, 255));
                }
                break;
            case R.id.nonfatButton:
                if (userBeverage[CUP_SIZE] < 0) {
                    displayErrorMessage(1);
                } else if (userBeverage[MILK_TYPE] >= 0) {
                    displayErrorMessage(4);
                } else {
                    userBeverage[MILK_TYPE] = 1;
                    nonfatButton.setColorFilter(Color.argb(100, 0, 77, 255));
                }
                break;
            case R.id.soyButton:
                if (userBeverage[CUP_SIZE] < 0) {
                    displayErrorMessage(1);
                } else if (userBeverage[MILK_TYPE] >=  0) {
                    displayErrorMessage(4);
                } else {
                    userBeverage[MILK_TYPE] = 2;
                    soyButton.setColorFilter(Color.argb(100, 0, 77, 255));
                }
                break;
            /*
            * The following espresso buttons increases the # of shots in the user drink
             */
            case R.id.espButton:
                if (userBeverage[CUP_SIZE] < 0) {
                    displayErrorMessage(1);
                } else if (userBeverage[MILK_TYPE] < 0) {
                    displayErrorMessage(3);
                } else if (userBeverage[WHP_CRM] == 1) {
                    displayErrorMessage(5);
                } else if (userBeverage[REG_ESP] < 4) {
                    userBeverage[REG_ESP] = userBeverage[REG_ESP] + 1;
                    espButton.setColorFilter(Color.argb(100, 0, 77, 255));
                    numOfRegText.setText(Integer.toString(userBeverage[REG_ESP]));
                }
                break;
            case R.id.dcfButton:
                if (userBeverage[CUP_SIZE] < 0) {
                    displayErrorMessage(1);
                } else if (userBeverage[MILK_TYPE] < 0) {
                    displayErrorMessage(3);
                } else if (userBeverage[WHP_CRM] == 1) {
                    displayErrorMessage(5);
                } else if (userBeverage[DEC_ESP] < 4) {
                    userBeverage[DEC_ESP] = userBeverage[DEC_ESP] + 1;
                    dcfButton.setColorFilter(Color.argb(100, 0, 77, 255));
                    numOfDecafText.setText(Integer.toString(userBeverage[DEC_ESP]));
                }
                break;

            /*
            * The following buttons for steam, whip, and syrups set the appropriate attribute to 1
            * (true) for the user beverage
             */
            case R.id.steamButton:
                if (userBeverage[MILK_TYPE] < 0) {
                    displayErrorMessage(3);
                } else if (userBeverage[WHP_CRM] == 1) {
                    displayErrorMessage(5);
                } else {
                    userBeverage[STEAM] = 1;
                    steamButton.setColorFilter(Color.argb(100, 0, 77, 255));
                }
                break;
            case R.id.vanillaButton:
                if (userBeverage[CUP_SIZE] < 0) {
                    displayErrorMessage(1);
                } else if (userBeverage[MILK_TYPE] < 0) {
                    displayErrorMessage(3);
                } else if (userBeverage[WHP_CRM] == 1) {
                    displayErrorMessage(5);
                } else {
                    userBeverage[VAN_SHOT] = 1;
                    vanillaButton.setColorFilter(Color.argb(100, 0, 77, 255));
                }
                break;
            case R.id.hazelnutButton:
                if (userBeverage[CUP_SIZE] < 0) {
                    displayErrorMessage(1);
                } else if (userBeverage[MILK_TYPE] < 0) {
                    displayErrorMessage(3);
                } else if (userBeverage[WHP_CRM] == 1) {
                    displayErrorMessage(5);
                } else {
                    userBeverage[HAZEL_SHOT] = 1;
                    hazelnutButton.setColorFilter(Color.argb(100, 0, 77, 255));
                }
                break;
            case R.id.chocolateButton:
                if (userBeverage[CUP_SIZE] < 0) {
                    displayErrorMessage(1);
                } else if (userBeverage[MILK_TYPE] < 0) {
                    displayErrorMessage(3);
                } else if (userBeverage[WHP_CRM] == 1) {
                    displayErrorMessage(5);
                } else {
                    userBeverage[CHOC_SHOT] = 1;
                    chocolateButton.setColorFilter(Color.argb(100, 0, 77, 255));
                }
                break;
            case R.id.toffeeButton:
                if (userBeverage[CUP_SIZE] < 0) {
                    displayErrorMessage(1);
                } else if (userBeverage[MILK_TYPE] < 0) {
                    displayErrorMessage(3);
                } else if (userBeverage[WHP_CRM] == 1) {
                    displayErrorMessage(5);
                } else {
                    userBeverage[TOFFEE_SHOT] = 1;
                    toffeeButton.setColorFilter(Color.argb(100, 0, 77, 255));
                }
                break;
            case R.id.peppermintButton:
                if (userBeverage[CUP_SIZE] < 0) {
                    displayErrorMessage(1);
                } else if (userBeverage[MILK_TYPE] < 0) {
                    displayErrorMessage(3);
                } else if (userBeverage[WHP_CRM] == 1) {
                    displayErrorMessage(5);
                } else {
                    userBeverage[PEP_SHOT] = 1;
                    peppermintButton.setColorFilter(Color.argb(100, 0, 77, 255));
                }
                break;
            case R.id.caramelButton:
                if (userBeverage[CUP_SIZE] < 0) {
                    displayErrorMessage(1);
                } else if (userBeverage[MILK_TYPE] < 0) {
                    displayErrorMessage(3);
                } else if (userBeverage[WHP_CRM] == 1) {
                    displayErrorMessage(5);
                } else {
                    userBeverage[CARA_SHOT] = 1;
                    caramelButton.setColorFilter(Color.argb(100, 0, 77, 255));
                }
                break;
            case R.id.whipButton:
                if (userBeverage[CUP_SIZE] < 0) {
                    displayErrorMessage(1);
                } else if (userBeverage[MILK_TYPE] < 0) {
                    displayErrorMessage(3);
                } else {
                    userBeverage[WHP_CRM] = 1;
                    whipCreamButton.setColorFilter(Color.argb(100, 0, 77, 255));
                }
                break;
        }

    }

    /*
    * setGamePlayButtonsEnabled will enable all of the game play buttons when enabled == true, otherwise
    * it will disable all of the game play buttons.
     */
    private void setGamePlayButtonsEnabled(boolean enabled) {
        for (int i = 0; i < gameLayout.getChildCount(); i++) {
            View child = gameLayout.getChildAt(i);
            child.setEnabled(enabled);
        }
    }

    /*
    * Dispalys an error message on the screen.
    * 1 - Cup not selected
    * 2 - Cup already selected!
    * 3 - Milk not selected!
    * 4 - Milk already selected!
    * 5 - Drink is finished, give to customer!
     */
    private void displayErrorMessage(int errorType) {
        CountDownTimer c = new CountDownTimer(1000,100) {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                errorBox.setVisibility(View.INVISIBLE);
            }
        }.start();
        switch(errorType) {
            case 1:
                errorText.setText("Cup size not selected!");
                break;
            case 2:
                errorText.setText("Cup size already selected!");
                break;
            case 3:
                errorText.setText("Milk type not selected!");
                break;
            case 4:
                errorText.setText("Milk type already selected!");
                break;
            case 5:
                errorText.setText("Give drink to customer!");
                break;
            case 6:
                errorText.setText("You threw the drink away!");
                break;
        }
        errorBox.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause() {
        super.onPause();
        //stop mediaplayer:
        if (mp != null) {
            mp.release();
        }
        if (mpAlert != null) {
            mpAlert.release();
        }

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        startMediaPlayer();
    }

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(this, Levels.class);
        this.startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        updateUI();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void updateUI() {

    }



}
