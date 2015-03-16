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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.Timer;

//Class which controls Game-Screen functionality
public class Game extends Activity implements View.OnClickListener{
    /*
    * Lots of GUI elements to create references for
     */

    //MediaPlayer for music PlayBack
    MediaPlayer mp = null;

    //ImageButtons for the various game buttons
    ImageButton infoButton,handButton,trashButton,smallButton,medButton,largeButton,wholeButton,
            nonfatButton,soyButton,espButton,dcfButton,steamButton,caramelButton,
            hazelnutButton,vanillaButton,toffeeButton,peppermintButton,chocolateButton,
            whipCreamButton = null;

    //Vibrator for button feedback
    Vibrator vibrator = null;

    //ProgressBar for showing User's level progress
    ProgressBar levelProgress = null;

    //TextViews to display text elements throughout the game
    TextView levelProgressText,levelTimeRemaining,levelFinishedText,levelInfoTitle,
            levelInfoSubtitle,levelInfoObjective,numOfDecafText,numOfRegText,levelTitle,
            drinkDisplay = null;

    //Regular buttons to control menu flow
    Button continueButton,nextLevelButton,levelInfoBegin = null;

    //Layouts containing all of the buttons
    RelativeLayout playerGuideLayout,levelCompletionLayout,levelInfoLayout,gameLayout = null;

    //Number of drinks made by user
    int drinksMade = 0;

    //current level, highest level user has completed
    int level = 0;
    int maxLevelCompleted = 1;

    //All of the level information, title, subtitle, drinks required, and time limit
    String titleText = null;
    String subtitleText = null;
    long timeLimit = 0;
    int drinksGoal = 0;

    //CountDownTimer is used to track level time limits
    CountDownTimer countDownTimer = null;

    //Constant Integers to make array referencing clearer
    final int CUP_SIZE = 0;
    final int STEAM = 1;
    final int MILK_TYPE = 2;
    final int TOFFEE_SHOT = 3;
    final int CHOC_SHOT = 4;
    final int PEP_SHOT = 5;
    final int HAZEL_SHOT = 6;
    final int CARA_SHOT = 7;
    final int VAN_SHOT = 8;
    final int DEC_ESP = 9;
    final int REG_ESP = 10;
    final int WHP_CRM = 11;

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
    int[] customerOrder = new int[12];
    int[] userBeverage = {-1,0,-1,0,0,0,0,0,0,0,0,0};
    int[] emptyArray = {-1,0,-1,0,0,0,0,0,0,0,0,0};
    int[] previousBeverage = new int[12];



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
    public void runTutorial() {
        //Fills in the level's TextViews with appropriate tutorial level text
        levelInfoTitle.setText(getString(R.string.tutorialLevelTitle));
        levelInfoObjective.setText(getString(R.string.tutorialLevelObjective));
        levelTitle.setText(getString(R.string.tutorialLevelTitle));

        //Makes the information screen visible, and hides the progress bar/countdown for this tutorial
        //level
        levelInfoLayout.setVisibility(View.VISIBLE);
        levelProgress.setVisibility(View.INVISIBLE);
        levelTimeRemaining.setVisibility(View.INVISIBLE);
    }

    /*
    * displayObjectives is used to display the level's objectives to the user before the game begins
     */
    public void displayObjectives() {
        levelInfoTitle.setText(titleText);
        if (subtitleText != null) {
            levelInfoSubtitle.setText(subtitleText);
        }
        levelInfoBegin.setText("Begin Level");
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

        levelInfoLayout.setVisibility(View.VISIBLE);
    }

    /*
    * retrieveLevelInfo retrieves and stores all of the level information that was passed to the
    * Game screen from the Level selection screen.
     */
    public void retrieveLevelInfo() {
        Intent i = getIntent();
        level = Integer.valueOf(i.getStringExtra("level"));
        maxLevelCompleted = Integer.valueOf(i.getStringExtra("maxLevelCompleted"));
        titleText = i.getStringExtra("levelTitle");
        subtitleText = i.getStringExtra("levelSubtitle");
        drinksGoal = Integer.valueOf(i.getStringExtra("numOfDrinks"));
        timeLimit = Long.valueOf(i.getStringExtra("timeLimit")) * 1000;
    }

    /*
    * setUpLevel sets up the level by updating the textviews, progress bar, and timer with the
    * information for the current level.
     */
    public void setUpLevel() {
        levelTitle.setText(titleText);
        levelProgress.setMax(drinksGoal);
        levelProgress.setVisibility(View.VISIBLE);
        levelProgressText.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(timeLimit, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                levelTimeRemaining.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                levelFinished(false);
            }
        };
    }

    /*
    * beginLevel begins the level by generating a drink order, displaying the drink order, starting
    * music playback, and beginning the countdown timer if the level requires it.
     */
    public void beginLevel() {
        gameLayout.setAlpha(1.0f);
        levelInfoLayout.setVisibility(View.INVISIBLE);
        if(level == 0) {
            customerOrder = emptyArray.clone();
            customerOrder[CUP_SIZE] = 2;
            customerOrder[MILK_TYPE] = 0;
            customerOrder[STEAM] = 1;
            displayDrinkOrder();
        } else {
            randomDrinkGen();
        }
        displayDrinkOrder();

        countDownTimer.start();

    }

    public void startMediaPlayer() {
        mp = MediaPlayer.create(this, R.raw.catwalk);
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
    public void randomDrinkGen() {
        int[] drink = new int[12];
        Random rand = new Random();
        switch(level) {
            case 12:
            case 11:
            case 10:
            case 9: //levels 9 and above add the possibility for the bottom row of syrups
                drink[TOFFEE_SHOT] = rand.nextInt(2);
                drink[CHOC_SHOT] = rand.nextInt(2);
                drink[PEP_SHOT] = rand.nextInt(2);
            case 8:
            case 7: //levels 7 and above add the possibility for the top row of syrups
                drink[CARA_SHOT] = rand.nextInt(2);
                drink[HAZEL_SHOT] = rand.nextInt(2);
                drink[VAN_SHOT] = rand.nextInt(2);
            case 6:
            case 5: //levels 5 and above add the possibility for 0-4 decaf shots of espresso

                drink[DEC_ESP] = rand.nextInt(5);
            case 4:
            case 3: //levels 3 and above add the possibility for 0-4 regular shots of espresso
                drink[REG_ESP] = rand.nextInt(5);
            case 2: //levels 2 and above add the possibility for whip cream
                drink[WHP_CRM] = rand.nextInt(2);
            case 1: //levels 1 and above require a cup size, possibility for steaming, and require
                    //a milk type
                drink[CUP_SIZE] = rand.nextInt(3);
                drink[STEAM] = rand.nextInt(2);
                drink[MILK_TYPE] = rand.nextInt(3);
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
        if(customerOrder[STEAM] == 1) drinkOrder.append(getString(R.string.steamed) + " ");
        switch(customerOrder[MILK_TYPE]) {
            case 0:
                drinkOrder.append(getString(R.string.whole) + "\n");
                break;
            case 1:
                drinkOrder.append(getString(R.string.nonfat) +"\n");
                break;
            case 2:
                drinkOrder.append(getString(R.string.soy) + "\n");
                break;
        }
        if(customerOrder[WHP_CRM] == 1) drinkOrder.append(getString(R.string.whip) + "\n");
        if(customerOrder[DEC_ESP] > 0) drinkOrder.append(Integer.toString(customerOrder[9]) + " " + getString(R.string.decaf) + "\n");
        if(customerOrder[REG_ESP] > 0) drinkOrder.append(Integer.toString(customerOrder[10]) + " " + getString(R.string.regular) + "\n");
        if(customerOrder[CARA_SHOT] == 1) drinkOrder.append(getString(R.string.caramel) + "     ");
        if(customerOrder[HAZEL_SHOT] == 1) drinkOrder.append(getString(R.string.hazelnut) +"     ");
        if(customerOrder[VAN_SHOT] == 1) drinkOrder.append(getString(R.string.vanilla) + "     ");
        if(customerOrder[TOFFEE_SHOT] == 1) drinkOrder.append(getString(R.string.toffee) + "      ");
        if(customerOrder[PEP_SHOT] == 1) drinkOrder.append(getString(R.string.peppermint) + "      ");
        if(customerOrder[CHOC_SHOT] == 1) drinkOrder.append(getString(R.string.chocolate) + "     ");

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
        if(drinksMade == drinksGoal) {
            countDownTimer.cancel();
            levelFinished(true);
        }
    }

    /*
    * levelFinished is used to display a message to the user that lets them know they finished the
    * level objectives, or ran out of time to complete the level. It also updates the save file
    * if the user has increased their maximum completed level.
     */
    public void levelFinished(boolean metObjectives) {
        levelCompletionLayout.setVisibility(View.VISIBLE);
        gameLayout.setAlpha(0.3f);
        drinkDisplay.setText("");
        levelProgress.setVisibility(View.INVISIBLE);
        levelProgressText.setVisibility(View.INVISIBLE);
        levelTimeRemaining.setText("");
        if(metObjectives) {
            levelCompletionLayout.setBackgroundColor(Color.parseColor("#ff1daf13"));
            levelFinishedText.setText(getString(R.string.levelPassedText) + " " + titleText + "!");
            level++;
            if(level > maxLevelCompleted) {
                updateSaveFile();
            }
        } else {
            levelCompletionLayout.setBackgroundColor(Color.parseColor("#ffca1a24"));
            levelFinishedText.setText(getString(R.string.levelFailedText) + " " + titleText + "!");
        }

    }

    /*
    * updateSaveFile updates the game's save file to store the user's current level.
     */
    public void updateSaveFile() {
        String fileName = "ultimatebarista.txt";
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

        continueButton = (Button) findViewById(R.id.continueButton);
        playerGuideLayout = (RelativeLayout) findViewById(R.id.playerGuideLayout);
        levelCompletionLayout = (RelativeLayout) findViewById(R.id.levelCompletionLayout);
        nextLevelButton = (Button) findViewById(R.id.nextLevelButton);
        infoButton = (ImageButton) findViewById(R.id.infoButton);

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
    }

    /*
    * setClickListeners links all of the GUI's buttons with the click listeners
     */
    public void setClickListeners() {
        continueButton.setOnClickListener(this);

        nextLevelButton.setOnClickListener(this);

        infoButton.setOnClickListener(this);

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
        levelProgressText.setText("");
        switch (v.getId()) {
            case R.id.infoButton:
                playerGuideLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.nextLevelButton:
                Intent i = new Intent(this, Levels.class);
                startActivity(i);
                this.finish();
                break;
            case R.id.continueButton:
                playerGuideLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.levelInfoBegin:
                beginLevel();
                break;
            case R.id.handButton:
                clearSelected();
                //if drinks are the same, increase counter, update level progress, and generate new
                //drink order
                if(compareDrinks()) {
                    levelProgressText.setText(getString(R.string.correctDrink));
                    drinksMade++;

                    previousBeverage = customerOrder.clone();
                    boolean sameDrink = false;
                    // generates a new drink, and checks to make sure that it is not the same drink
                    // that the user just made. If the drink is the same, generates a new drink again
                    // otherwise, loop exists and the new drink order is displayed
                    do {
                        randomDrinkGen();
                        for(int pos=0; pos < previousBeverage.length; pos++) {
                            if(customerOrder[pos] == previousBeverage[pos]) {
                                sameDrink = true;
                            }
                        }
                    } while(!sameDrink);
                    displayDrinkOrder();

                    updateLevelProgress();
                } else { //drink is incorrect, inform user
                    levelProgressText.setText(getString(R.string.incorrectDrink));
                    vibrator.vibrate(400);
                }
                //clear the user's created beverage
                userBeverage = emptyArray.clone();
                break;

            case R.id.trashButton:
                clearSelected();
                //clear user's created beverage, and inform user
                userBeverage = emptyArray.clone();
                levelProgressText.setText(getString(R.string.drinkThrownOut));
                break;

            /*
            * The following buttons set the drink size for the user beverage, if size is already
            * selected an error message is generated
             */
            case R.id.smallButton:
                if(userBeverage[CUP_SIZE] >= 0) {
                    levelProgressText.setText(getString(R.string.cupAlreadySelected));
                } else {
                    userBeverage[CUP_SIZE] = 0;
                    smallButton.setColorFilter(Color.argb(100, 0, 77,255));
                }
                break;
            case R.id.medButton:
                if(userBeverage[CUP_SIZE] >= 0) {
                    levelProgressText.setText(getString(R.string.cupAlreadySelected));
                } else {
                    userBeverage[CUP_SIZE] = 1;
                    medButton.setColorFilter(Color.argb(100, 0, 77,255));
                }
                break;
            case R.id.largeButton:
                if(userBeverage[CUP_SIZE] >= 0) {
                    levelProgressText.setText(getString(R.string.cupAlreadySelected));
                } else {
                    userBeverage[CUP_SIZE] = 2;
                    largeButton.setColorFilter(Color.argb(100, 0, 77,255));
                }
                break;

            /*
            * The following buttons select the milk type for the user beverage, if a milk type
            * was already selected an error is generated
             */
            case R.id.wholeButton:
                if(userBeverage[MILK_TYPE] >= 0) {
                    levelProgressText.setText(getString(R.string.milkAlreadySelected));
                } else {
                    userBeverage[MILK_TYPE] = 0;
                    wholeButton.setColorFilter(Color.argb(100, 0, 77,255));
                }
                break;
            case R.id.nonfatButton:
                if(userBeverage[MILK_TYPE] >= 0) {
                    levelProgressText.setText(getString(R.string.milkAlreadySelected));
                } else {
                    userBeverage[MILK_TYPE] = 1;
                    nonfatButton.setColorFilter(Color.argb(100, 0, 77,255));
                }
                break;
            case R.id.soyButton:
                if(userBeverage[MILK_TYPE] >= 0) {
                    levelProgressText.setText(getString(R.string.milkAlreadySelected));
                } else {
                    userBeverage[MILK_TYPE] = 2;
                    soyButton.setColorFilter(Color.argb(100, 0, 77,255));
                }
                break;
            /*
            * The following espresso buttons increases the # of shots in the user drink
             */
            case R.id.espButton:
                userBeverage[REG_ESP] = userBeverage[REG_ESP] + 1;
                espButton.setColorFilter(Color.argb(100, 0, 77,255));
                numOfRegText.setText(Integer.toString(userBeverage[REG_ESP]));
                break;
            case R.id.dcfButton:
                userBeverage[DEC_ESP] = userBeverage[DEC_ESP] + 1;
                dcfButton.setColorFilter(Color.argb(100, 0, 77,255));
                numOfDecafText.setText(Integer.toString(userBeverage[DEC_ESP]));
                break;

            /*
            * The following buttons for steam, whip, and syrups set the appropriate attribute to 1
            * (true) for the user beverage
             */
            case R.id.steamButton:
                userBeverage[STEAM] = 1;
                steamButton.setColorFilter(Color.argb(100, 0, 77,255));
                break;
            case R.id.vanillaButton:
                userBeverage[VAN_SHOT] = 1;
                vanillaButton.setColorFilter(Color.argb(100, 0, 77,255));
                break;
            case R.id.hazelnutButton:
                userBeverage[HAZEL_SHOT] = 1;
                hazelnutButton.setColorFilter(Color.argb(100, 0, 77,255));
                break;
            case R.id.chocolateButton:
                userBeverage[CHOC_SHOT] = 1;
                chocolateButton.setColorFilter(Color.argb(100, 0, 77,255));
                break;
            case R.id.toffeeButton:
                userBeverage[TOFFEE_SHOT] = 1;
                toffeeButton.setColorFilter(Color.argb(100, 0, 77,255));
                break;
            case R.id.peppermintButton:
                userBeverage[PEP_SHOT] = 1;
                peppermintButton.setColorFilter(Color.argb(100, 0, 77,255));
                break;
            case R.id.caramelButton:
                userBeverage[CARA_SHOT] = 1;
                caramelButton.setColorFilter(Color.argb(100, 0, 77,255));
                break;
            case R.id.whipButton:
                userBeverage[WHP_CRM] = 1;
                whipCreamButton.setColorFilter(Color.argb(100, 0, 77,255));
                break;
        }

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
        // super.onBackPressed(); // Comment this super call to avoid calling finish()
    }


}
