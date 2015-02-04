package edu.oakland.ultimatebarista;

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


public class Game extends Activity implements View.OnClickListener{
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

    Button steamButton = null;

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

    int[] customerOrder = new int[12];
    int[] userBeverage = {-1,0,-1,0,0,0,0,0,0,0,0,0};
    int[] emptyArray = {-1,0,-1,0,0,0,0,0,0,0,0,0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


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

        handButton.setOnClickListener(this);
        trashButton.setOnClickListener(this);

        smallButton.setOnClickListener(this);
        medButton.setOnClickListener(this);
        largeButton.setOnClickListener(this);

        nonfatButton.setOnClickListener(this);
        wholeButton.setOnClickListener(this);
        soyButton.setOnClickListener(this);

        steamButton.setOnClickListener(this);

        Intent i = getIntent();
        String level = i.getStringExtra("level");

        levelTitle.setText(level);


        /*
        Drink Example

        Int[] Drink = {CUP_SIZE, STEAM, MILK_TYPE, WHT_CHOC_SHOT, CHOC_SHOT, PEP_SHOT, HAZEL_SHOT, CARA_SHOT, VAN_SHOT, DEC_ESP, REG_ESP, WHP_CRM}
        Where   CUP_SIZE is 0: small, 1: medium, 2: large
                MILK_TYPE is 0: Whole, 1: Nonfat, 2: Soy
                STEAM is 0: none, 1: steamed
                WHT_CHOC_SHOT is 0: none, 1: added
                ---same for evey syrup shot---
                Chocolate, Peppermint, Hazelnut, Caramel, Vanilla
                DEC_ESP is x: where x is the amount of shots added
                REG_ESP is y: where y is the amount of shots added
                WHP_CRM is 0: none, 1: added
         */

        randomDrinkGen(1);
        displayDrinkOrder();

    }
    public void randomDrinkGen(int level) {
        int[] drink = new int[12];
        Random rand = new Random();
        drink[CUP_SIZE] = rand.nextInt(3);
        drink[STEAM] = rand.nextInt(2);
        drink[MILK_TYPE] = rand.nextInt(3);
        if(level > 1) {
            drink[WHT_CHOC_SHOT] = rand.nextInt(2);
            drink[CHOC_SHOT] = rand.nextInt(2);
            drink[PEP_SHOT] = rand.nextInt(2);
            drink[HAZEL_SHOT] = rand.nextInt(2);
            drink[CARA_SHOT] = rand.nextInt(2);
            drink[VAN_SHOT] = rand.nextInt(2);
            drink[DEC_ESP] = rand.nextInt(5);
            drink[REG_ESP] = rand.nextInt(5);
            drink[WHP_CRM] = rand.nextInt(2);
        }
        customerOrder = drink;
    }

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
        if(customerOrder[DEC_ESP] > 0) drinkOrder.append(Integer.toString(customerOrder[9]) + " Regular\n");
        if(customerOrder[REG_ESP] > 0) drinkOrder.append(Integer.toString(customerOrder[10]) + " Decaf\n");
        if(customerOrder[WHP_CRM] == 1) drinkOrder.append("Whip Cream\n");

        drinkDisplay.setText(drinkOrder.toString());

    }

    @Override
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
        }

    }

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
