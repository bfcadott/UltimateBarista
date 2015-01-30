package edu.oakland.ultimatebarista;

//import list, brings in needed functionality
import android.app.Activity;
import android.content.Intent;
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
    TextView titleText = null;

    //Stringbuffer for reading in save file
    final StringBuffer storedString = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        //Creates title text TextBox
        titleText = (TextView) findViewById(R.id.titleText);

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

        titleText.setText(storedString);


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
        switch (Integer.valueOf(storedString.toString())) {
            case 12:
                level12.setEnabled(true);
            case 11:
                level11.setEnabled(true);
            case 10:
                level10.setEnabled(true);
            case 9:
                level9.setEnabled(true);
            case 8:
                level8.setEnabled(true);
            case 7:
                level7.setEnabled(true);
            case 6:
                level6.setEnabled(true);
            case 5:
                level5.setEnabled(true);
            case 4:
                level4.setEnabled(true);
            case 3:
                level3.setEnabled(true);
            case 2:
                level2.setEnabled(true);
            case 1:
                level1.setEnabled(true);

        }




    }

    @Override
    public void onClick(View v) {
        //Listener decides what level to load based on which button throws the onClick event
        Intent i = new Intent(this, Game.class);
        switch (v.getId()) {
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
        }

        this.startActivity(i);
    }
}
