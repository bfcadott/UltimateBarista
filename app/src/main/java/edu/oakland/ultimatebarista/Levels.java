package edu.oakland.ultimatebarista;

//import list, brings in needed functionality
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Levels extends Activity implements View.OnClickListener {
    //Filename for our level progress save file
    String fileName = "ultimatebarista.txt";


    private MediaPlayer mp;

    //Creates all imageButtons
    ImageButton level1 = null;
    ImageButton level2 = null;
    ImageButton level3 = null;
    ImageButton level4 = null;
    ImageButton level5 = null;
    ImageButton level6 = null;
    ImageButton level7 = null;
    ImageButton level8 = null;
    ImageButton level9 = null;
    ImageButton level10 = null;
    ImageButton level11 = null;
    ImageButton level12 = null;

    int level = 1;

    //Stringbuffer for reading in save file
    final StringBuffer storedString = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        //Creates title text TextBox

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




        //Defines all ImageButtons based on assets in the GUI

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

        //Links all buttons to appropriate listeners
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

        //Set all levels to FALSE enabled, this prevents players
        //from playing a level they have yet to unlock
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
        
        //Reads the integer from the save file and unlocks levels
        //based on user progress

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
        }

        mp = MediaPlayer.create(this, R.raw.levelselection);
        mp.setLooping(true);
        mp.start();



    }

    @Override
    public void onClick(View v) {
        //Listener decides what level to load based on which button throws the onClick event
        Intent i = new Intent(this, Game.class);
        i.putExtra("maxLevelCompleted",String.valueOf(level));
        switch (v.getId()) {
            case R.id.level1button:
                i.putExtra("level","1");
                i.putExtra("levelTitle","Barista Basics");
                i.putExtra("numOfDrinks","3");

                break;
            case R.id.level2button:
                i.putExtra("level","2");
                i.putExtra("numOfDrinks","5");
                i.putExtra("levelTitle","Wonderful Whip");
                break;
            case R.id.level3button:
                i.putExtra("level","3");
                i.putExtra("numOfDrinks","5");
                i.putExtra("timeLimit","60");
                i.putExtra("levelTitle","Enticing Espresso");
                break;
            case R.id.level4button:
                i.putExtra("level","4");
                i.putExtra("numOfDrinks","6");
                i.putExtra("timeLimit","60");
                i.putExtra("levelTitle","Demure Decaf");
                break;
            case R.id.level5button:
                i.putExtra("level","5");
                i.putExtra("numOfDrinks","5");
                i.putExtra("timeLimit","60");
                break;
            case R.id.level6button:
                i.putExtra("level","6");
                i.putExtra("numOfDrinks","7");
                i.putExtra("timeLimit","50");

                break;
            case R.id.level7button:
                i.putExtra("level","7");
                i.putExtra("numOfDrinks","4");
                i.putExtra("levelTitle","Sweet Syrup Sensations");
                i.putExtra("timeLimit","60");
                break;
            case R.id.level8button:
                i.putExtra("level","8");
                i.putExtra("numOfDrinks","6");
                i.putExtra("timeLimit","60");
                break;
            case R.id.level9button:
                i.putExtra("level","9");
                i.putExtra("numOfDrinks","8");
                i.putExtra("levelTitle","Sweet Syrup Sensations 2");
                i.putExtra("levelSubtitle","The Sweetening");
                i.putExtra("timeLimit","50");
                break;
            case R.id.level10button:
                i.putExtra("level","10");
                i.putExtra("numOfDrinks","12");
                i.putExtra("timeLimit","60");
                break;
            case R.id.level11button:
                i.putExtra("level","11");
                i.putExtra("numOfDrinks","16");
                i.putExtra("timeLimit","50");
                break;
            case R.id.level12button:
                i.putExtra("level","12");
                i.putExtra("numOfDrinks","20");
                i.putExtra("timeLimit","60");
                break;
        }

        this.startActivity(i);
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
    }
    @Override
    public void onRestart() {
        super.onRestart();  // Always call the superclass method first
        mp.start();
    }
}
