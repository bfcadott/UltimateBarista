package edu.oakland.ultimatebarista;

//Imports allow usage of built-in functionality
import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Random;

//Class which controls Game-Screen functionality
public class Game extends Activity implements View.OnClickListener{
    
    //Initalizing Buttons and Text boxes
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

    //Placeholder Integers to make array referencing quick and easy
    int CUP_SIZE = 0;
    int STEAM = 1;
    int MILK_TYPE = 2;
    int WHT_CHOC_SHOT = 3;
    int CHOC_SHOT = 4;
    int PEP_SHOT = 5;
    int HAZEL_SHOT = 6;
    int CARA_SHOT = 7;
    int VAN_SHOT = 8;
    int DEC_ESP = 9;
    int REG_ESP = 10;
    int WHP_CRM = 11;

    //Three arrays to hold: Customer Drink, User Drink, and the empty array to help reinitalize user drink
    int[] customerOrder = new int[12];
    int[] userBeverage = {-1,0,-1,0,0,0,0,0,0,0,0,0};
    int[] emptyArray = {-1,0,-1,0,0,0,0,0,0,0,0,0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Creation of buttons and text boxes and linkage to their GUI asset
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

        //Adding all buttons to the Class Listener
        
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



        //Retrieve the Level number from the Intent
        Intent i = getIntent();
        String level = i.getStringExtra("level");

        levelTitle.setText(level);


        /*
        Drink Example

        Int[] Drink = {CUP_SIZE, STEAM, MILK_TYPE, WHT_CHOC_SHOT, CHOC_SHOT, PEP_SHOT, HAZEL_SHOT, CARA_SHOT, VAN_SHOT, DEC_ESP, REG_ESP, WHP_CRM}
        Where   CUP_SIZE is 0: small, 1: medium, 2: large
                MILK_TYPE is 0: whole, 1: Nonfat, 2: Soy
                STEAM is 0: none, 1: steamed
                WHT_CHOC_SHOT is 0: none, 1: added
                ---same for evey syrup shot---
                Chocolate, Peppermint, Hazelnut, Caramel, Vanilla
                DEC_ESP is x: where x is the amount of shots added
                REG_ESP is y: where y is the amount of shots added
                WHP_CRM is 0: none, 1: added
         */

        randomDrinkGen(Integer.valueOf(level));
        displayDrinkOrder();

    }
    //Generates a random drink based on the argument of whichever level user is playing
    public void randomDrinkGen(int level) {
        int[] drink = new int[12];
        Random rand = new Random();
        drink[CUP_SIZE] = rand.nextInt(3);
        drink[STEAM] = rand.nextInt(2);
        drink[MILK_TYPE] = rand.nextInt(3);
        drink[DEC_ESP] = rand.nextInt(5);
        drink[REG_ESP] = rand.nextInt(5);

        if(level > 1) {
            drink[WHT_CHOC_SHOT] = rand.nextInt(2);
            drink[CHOC_SHOT] = rand.nextInt(2);
            drink[PEP_SHOT] = rand.nextInt(2);
            drink[HAZEL_SHOT] = rand.nextInt(2);
            drink[CARA_SHOT] = rand.nextInt(2);
            drink[VAN_SHOT] = rand.nextInt(2);
            drink[WHP_CRM] = rand.nextInt(2);
        }
        customerOrder = drink;
    }
    
    //Reads the drink the customer wants and displays it to the text box on screen
    public void displayDrinkOrder() {
        drinkDisplay = (TextView) findViewById(R.id.drinkDisplay);
        StringBuffer drinkOrder = new StringBuffer();
        switch(customerOrder[CUP_SIZE]) {
            case 0:
                drinkOrder.append("Small Latte with: \n");
                break;
            case 1:
                drinkOrder.append("Medium Latte with: \n");
                break;
            case 2:
                drinkOrder.append("Large Latte with:\n");
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
        if(customerOrder[WHT_CHOC_SHOT] == 1) drinkOrder.append("White Chocolate\n");
        if(customerOrder[CHOC_SHOT] == 1) drinkOrder.append("Chocolate\n");
        if(customerOrder[PEP_SHOT] == 1) drinkOrder.append("Peppermint\n");
        if(customerOrder[HAZEL_SHOT] == 1) drinkOrder.append("Hazelnut\n");
        if(customerOrder[CARA_SHOT] == 1) drinkOrder.append("Caramel\n");
        if(customerOrder[VAN_SHOT] == 1) drinkOrder.append("Vanilla\n");
        if(customerOrder[DEC_ESP] > 0) drinkOrder.append(Integer.toString(customerOrder[9]) + " Decaf\n");
        if(customerOrder[REG_ESP] > 0) drinkOrder.append(Integer.toString(customerOrder[10]) + " Regular\n");
        if(customerOrder[WHP_CRM] == 1) drinkOrder.append("Whip Cream\n");

        drinkDisplay.setText(drinkOrder.toString());

    }

    @Override
    
    /*
    Class Listener, takes all button input and switches cases based on which button threw the event
    HandButton: Checks if player drink is equal to customer drink; if yes, succeed, if no, fail
    TrashButton: Sets the player drink equal to the empty array, allowing them to start over
    Small/Med/Large Button: Allows user to grab a cup of that size
    All other buttons add something to the drink
    */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.handButton:
                if(compareDrinks()) {
                    levelTitle.setText("You are a winner");
                    randomDrinkGen(1);
                    displayDrinkOrder();
                } else {
                    levelTitle.setText("You are a loser!");
                }
                userBeverage = emptyArray.clone();
                break;
            case R.id.trashButton:
                userBeverage = emptyArray.clone();
                levelTitle.setText("You dun goofed");
                break;
            case R.id.smallButton:
                if(userBeverage[CUP_SIZE] >= 0) {
                    levelTitle.setText("ERROR");
                } else {
                    userBeverage[CUP_SIZE] = 0;
                }
                break;
            case R.id.medButton:
                if(userBeverage[CUP_SIZE] >= 0) {
                    levelTitle.setText("ERROR");
                } else {
                    userBeverage[CUP_SIZE] = 1;
                }
                break;
            case R.id.largeButton:
                if(userBeverage[CUP_SIZE] >= 0) {
                    levelTitle.setText("ERROR");
                } else {
                    userBeverage[CUP_SIZE] = 2;
                }
                break;

            case R.id.wholeButton:
                if(userBeverage[MILK_TYPE] >= 0) {
                    levelTitle.setText("ERROR");
                } else {
                    userBeverage[MILK_TYPE] = 0;
                }
                break;
            case R.id.nonfatButton:
                if(userBeverage[MILK_TYPE] >= 0) {
                    levelTitle.setText("ERROR");
                } else {
                    userBeverage[MILK_TYPE] = 1;
                }
                break;
            case R.id.soyButton:
                if(userBeverage[MILK_TYPE] >= 0) {
                    levelTitle.setText("ERROR");
                } else {
                    userBeverage[MILK_TYPE] = 2;
                }
                break;

            case R.id.steamButton:
                userBeverage[STEAM] = 1;
                break;

            case R.id.espButton:
                userBeverage[REG_ESP] = userBeverage[REG_ESP] + 1;
                break;

            case R.id.dcfButton:
                userBeverage[DEC_ESP] = userBeverage[DEC_ESP] + 1;
                break;
        }

    }

    //Loops through both arrays and decides if the two are equal
    public boolean compareDrinks() {
        int i = 0;

        while(i < customerOrder.length) {
            if(customerOrder[i] != userBeverage [i]) {
                levelTitle.setText("You are a failure!");
                return false;
            }
            i++;
        }
        return true;
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
}
