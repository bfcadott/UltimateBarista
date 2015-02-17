package edu.oakland.ultimatebarista;

//Imports allow usage of built-in functionality
import android.app.Activity;
import android.content.Intent;
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

//Class which controls Game-Screen functionality
public class Game extends Activity implements View.OnClickListener{
    
    //Initalizing GUI elements
    MediaPlayer mp;

    ImageButton infoButton = null;

    TextView levelTitle = null;
    TextView drinkDisplay = null;
    ImageButton handButton = null;
    ImageButton trashButton = null;
    ImageButton smallButton = null;
    ImageButton medButton = null;
    ImageButton largeButton = null;

    ImageButton wholeButton = null;
    ImageButton nonfatButton = null;
    ImageButton soyButton = null;

    Button espButton = null;
    Button dcfButton = null;

    Button steamButton = null;

    Vibrator vibrator = null;

    ImageButton caramelButton = null;
    ImageButton hazelnutButton = null;
    ImageButton vanillaButton = null;
    ImageButton whitechocolateButton = null;
    ImageButton peppermintButton = null;
    ImageButton chocolateButton = null;
    ImageButton whipCreamButton = null;

    TextView levelProgressText = null;
    ProgressBar levelProgress = null;
    TextView levelTimeRemaining = null;

    Button continueButton = null;
    Button nextLevelButton = null;
    RelativeLayout playerGuideLayout = null;
    RelativeLayout levelCompletionLayout = null;

    //Placeholder Integers to make array referencing quick and easy
    final int CUP_SIZE = 0;
    final int STEAM = 1;
    final int MILK_TYPE = 2;
    final int WHT_CHOC_SHOT = 3;
    final int CHOC_SHOT = 4;
    final int PEP_SHOT = 5;
    final int HAZEL_SHOT = 6;
    final int CARA_SHOT = 7;
    final int VAN_SHOT = 8;
    final int DEC_ESP = 9;
    final int REG_ESP = 10;
    final int WHP_CRM = 11;


    //Three arrays to hold: Customer Drink, User Drink, and the empty array to help reinitalize user drink
     /*
    *   Drink Example
    *
    *   Int[] Drink = {CUP_SIZE, STEAM, MILK_TYPE, WHT_CHOC_SHOT, CHOC_SHOT, PEP_SHOT, HAZEL_SHOT, CARA_SHOT, VAN_SHOT, DEC_ESP, REG_ESP, WHP_CRM}
    *   Where   CUP_SIZE is 0: small, 1: medium, 2: large
    *       MILK_TYPE is 0: whole, 1: Nonfat, 2: Soy
    *       STEAM is 0: none, 1: steamed
    *       WHT_CHOC_SHOT is 0: none, 1: added
    *       ---same for evey syrup shot---
    *       Chocolate, Peppermint, Hazelnut, Caramel, Vanilla
    *       DEC_ESP is x: where x is the amount of shots added
    *       REG_ESP is y: where y is the amount of shots added
    *       WHP_CRM is 0: none, 1: added
     */
    int[] customerOrder = new int[12];
    int[] userBeverage = {-1,0,-1,0,0,0,0,0,0,0,0,0};
    int[] emptyArray = {-1,0,-1,0,0,0,0,0,0,0,0,0};

    //Number of drinks made by user
    int drinksMade = 0;

    //current level, highest level user has completed
    int level = 1;
    int maxLevelCompleted = 1;


    //All of the level information, title, subtitle, drinks required, and time limit
    String titleText = null;
    String subtitleText = null;
    long timeLimit = 0;
    int drinksGoal = 0;
    CountDownTimer countDownTimer = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        linkUIElements();
        setClickListeners();

        retrieveLevelInfo();
        setUpLevel();

        beginLevel();
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
        if(level > 2) {
            timeLimit = Long.valueOf(i.getStringExtra("timeLimit")) * 1000;
        }
    }

    /*
    * setUpLevel sets up the level by updating the textviews, progress bar, and timer with the
    * information for the current level.
     */
    public void setUpLevel() {
        levelTitle.setText(titleText);
        levelProgress.setMax(drinksGoal);
        if(level == 1) {
            playerGuideLayout.setVisibility(View.VISIBLE);
        }
        if(level > 2) {
            countDownTimer = new CountDownTimer(timeLimit, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    levelTimeRemaining.setText(String.valueOf(millisUntilFinished/1000));
                }

                @Override
                public void onFinish() {
                    levelCompletionLayout.setVisibility(View.VISIBLE);

                }
            };
            levelTimeRemaining.setVisibility(View.VISIBLE);
        }

    }

    /*
    * beginLevel begins the level by generating a drink order, displaying the drink order, starting
    * music playback, and beginning the countdown timer if the level requires it.
     */
    public void beginLevel() {
        randomDrinkGen();
        displayDrinkOrder();

        if(level > 2) {
            countDownTimer.start();
        }
        mp = MediaPlayer.create(getApplicationContext(), R.raw.catwalk);
        mp.setLooping(true);
        mp.start();
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
                drink[WHT_CHOC_SHOT] = rand.nextInt(2);
                drink[CHOC_SHOT] = rand.nextInt(2);
                drink[PEP_SHOT] = rand.nextInt(2);
            case 8:
            case 7: //levels 7 and above add the possibility for the top row of syrups
                drink[CARA_SHOT] = rand.nextInt(2);
                drink[HAZEL_SHOT] = rand.nextInt(2);
                drink[VAN_SHOT] = rand.nextInt(2);
            case 6:
            case 5: //levels 5 and above add the possibility for 0-5 decaf shots of espresso
                drink[DEC_ESP] = rand.nextInt(5);
            case 4:
            case 3: //levels 3 and above add the possibility for 0-5 regular shots of espresso
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
                drinkOrder.append("Small\n");
                break;
            case 1:
                drinkOrder.append("Medium\n");
                break;
            case 2:
                drinkOrder.append("Large\n");
                break;
        }
        if(customerOrder[STEAM] == 1) drinkOrder.append("Steamed ");
        switch(customerOrder[MILK_TYPE]) {
            case 0:
                drinkOrder.append("Whole Milk\n");
                break;
            case 1:
                drinkOrder.append("Nonfat Milk\n");
                break;
            case 2:
                drinkOrder.append("Soy Milk\n");
                break;
        }
        if(customerOrder[CARA_SHOT] == 1) drinkOrder.append("Caramel     ");
        if(customerOrder[HAZEL_SHOT] == 1) drinkOrder.append("Hazelnut     ");
        if(customerOrder[VAN_SHOT] == 1) drinkOrder.append("Vanilla     ");
        if(customerOrder[WHT_CHOC_SHOT] == 1) drinkOrder.append("White Chocolate      ");
        if(customerOrder[PEP_SHOT] == 1) drinkOrder.append("Peppermint      ");
        if(customerOrder[CHOC_SHOT] == 1) drinkOrder.append("Chocolate     ");
        if(customerOrder[DEC_ESP] > 0) drinkOrder.append("\n" + Integer.toString(customerOrder[9]) + " Decaf");
        if(customerOrder[REG_ESP] > 0) drinkOrder.append("\n" + Integer.toString(customerOrder[10]) + " Regular");
        if(customerOrder[WHP_CRM] == 1) drinkOrder.append("\n Whip Cream");

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
                vibrator.vibrate(400);
                return false;
            }
            i++;
        }
        return true;
    }

    /*
    * updateLevelProgress Updates the progress bar for the level, if the level goals are satisfied
    * the level completion is set to visible, and the save file is updated.
     */
    public void updateLevelProgress() {
        if(drinksMade < drinksGoal) {
            levelProgress.setProgress(drinksMade);
        } else {
            levelProgress.setProgress(drinksMade);
            levelCompletionLayout.setVisibility(View.VISIBLE);
            level++;
            if(level > maxLevelCompleted) {
                updateSaveFile();
            }
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
    * linkUIElements links all of the GUI assets with the corresponding variable
     */
    public void linkUIElements() {

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

        steamButton = (Button) findViewById(R.id.steamButton);

        espButton = (Button) findViewById(R.id.espButton);
        dcfButton = (Button) findViewById(R.id.dcfButton);

        vanillaButton = (ImageButton) findViewById(R.id.vanillaButton);
        hazelnutButton = (ImageButton) findViewById(R.id.hazelnutButton);
        chocolateButton = (ImageButton) findViewById(R.id.chocolateButton);
        whitechocolateButton = (ImageButton) findViewById(R.id.whitechocolateButton);
        peppermintButton = (ImageButton) findViewById(R.id.peppermintButton);
        caramelButton = (ImageButton) findViewById(R.id.caramelButton);
        whipCreamButton = (ImageButton) findViewById(R.id.whipButton);
        levelProgress = (ProgressBar) findViewById(R.id.levelProgressBar);

        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        levelProgressText = (TextView) findViewById(R.id.levelProgressText);
    }

    /*
    * setClickListeners links all of the GUI's buttons with the click listeners
     */
    public void setClickListeners() {
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerGuideLayout.setVisibility(View.INVISIBLE);
            }
        });

        nextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), Levels.class);
                view.getContext().startActivity(i);
            }
        });


        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerGuideLayout.setVisibility(View.VISIBLE);
            }
        });

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
        whitechocolateButton.setOnClickListener(this);
        peppermintButton.setOnClickListener(this);
        caramelButton.setOnClickListener(this);
        whipCreamButton.setOnClickListener(this);


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

            case R.id.handButton:
                //if drinks are the same, increase counter, update level progress, and generate new
                //drink order
                if(compareDrinks()) {
                    levelProgressText.setText("Drink was correct!");
                    drinksMade++;
                    updateLevelProgress();
                    randomDrinkGen();
                    displayDrinkOrder();
                } else { //drink is incorrect, inform user
                    levelProgressText.setText("Drink is incorrect! Try again!");
                }

                //clear the user's created beverage
                userBeverage = emptyArray.clone();
                break;

            case R.id.trashButton:
                //clear user's created beverage, and inform user
                userBeverage = emptyArray.clone();
                levelProgressText.setText("You threw the drink away!");
                break;

            /*
            * The following buttons set the drink size for the user beverage, if size is already
            * selected an error message is generated
             */
            case R.id.smallButton:
                if(userBeverage[CUP_SIZE] >= 0) {
                    levelProgressText.setText("Cup size already selected!");
                } else {
                    userBeverage[CUP_SIZE] = 0;
                }
                break;
            case R.id.medButton:
                if(userBeverage[CUP_SIZE] >= 0) {
                    levelProgressText.setText("Cup size already selected!");
                } else {
                    userBeverage[CUP_SIZE] = 1;
                }
                break;
            case R.id.largeButton:
                if(userBeverage[CUP_SIZE] >= 0) {
                    levelProgressText.setText("Cup size already selected!");
                } else {
                    userBeverage[CUP_SIZE] = 2;
                    largeButton.setImageResource(R.drawable.largeselected);
                }
                break;

            /*
            * The following buttons select the milk type for the user beverage, if a milk type
            * was already selected an error is generated
             */
            case R.id.wholeButton:
                if(userBeverage[MILK_TYPE] >= 0) {
                    levelProgressText.setText("Milk type already selected!");
                } else {
                    userBeverage[MILK_TYPE] = 0;
                }
                break;
            case R.id.nonfatButton:
                if(userBeverage[MILK_TYPE] >= 0) {
                    levelProgressText.setText("Milk type already selected!");
                } else {
                    userBeverage[MILK_TYPE] = 1;
                }
                break;
            case R.id.soyButton:
                if(userBeverage[MILK_TYPE] >= 0) {
                    levelProgressText.setText("Milk type already selected!");
                } else {
                    userBeverage[MILK_TYPE] = 2;
                }
                break;
            /*
            * The following espresso buttons increases the # of shots in the user drink
             */
            case R.id.espButton:
                userBeverage[REG_ESP] = userBeverage[REG_ESP] + 1;
                break;
            case R.id.dcfButton:
                userBeverage[DEC_ESP] = userBeverage[DEC_ESP] + 1;
                break;

            /*
            * The following buttons for steam, whip, and syrups set the appropriate attribute to 1
            * (true) for the user beverage
             */
            case R.id.steamButton:
                userBeverage[STEAM] = 1;
                break;
            case R.id.vanillaButton:
                userBeverage[VAN_SHOT] = 1;
                break;
            case R.id.hazelnutButton:
                userBeverage[HAZEL_SHOT] = 1;
                break;
            case R.id.chocolateButton:
                userBeverage[CHOC_SHOT] = 1;
                break;
            case R.id.whitechocolateButton:
                userBeverage[WHT_CHOC_SHOT] = 1;
                break;
            case R.id.peppermintButton:
                userBeverage[PEP_SHOT] = 1;
                break;
            case R.id.caramelButton:
                userBeverage[CARA_SHOT] = 1;
                break;
            case R.id.whipButton:
                userBeverage[WHP_CRM] = 1;
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
        //stop mediaplayer:
        if (mp != null && mp.isPlaying()) {
            mp.stop();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        mp.start();
        // re-sync the clock with player...
    }

    @Override
    public void onRestart() {
        super.onRestart();  // Always call the superclass method first
        mp.start();
    }
}
