package edu.oakland.ultimatebarista;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;


public class Main extends Activity {
    Button login = null;
    String fileName = "ultimatebarista.txt";
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        login = (Button) findViewById(R.id.loginButton);
        File file = new File(this.getFilesDir(), fileName);
        if(!file.exists()) {
            String string = "1";
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(fileName, this.MODE_PRIVATE);
                outputStream.write(string.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), Levels.class);
                view.getContext().startActivity(i);
            }
        });

        mp = MediaPlayer.create(this, R.raw.mainscreen);
        mp.setLooping(true);
        mp.start();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
