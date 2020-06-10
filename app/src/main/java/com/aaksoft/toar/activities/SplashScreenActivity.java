package com.aaksoft.toar.activities;

/*
    Created By Aasharib
    on
    28 March, 2019
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.aaksoft.toar.R;

/*
    This activity contains logic for application splash screen.
*/

public class SplashScreenActivity extends AppCompatActivity{

    //Time for which splash should be displayed
    private static int SPLASH_SCREEN_TIMEOUT = 3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        new Handler().postDelayed(new Runnable() {  //start MapsActivity once time is done.
            @Override
            public void run() {
                Intent mapsActivity = new Intent(SplashScreenActivity.this, com.aaksoft.toar.activities.MapsActivity.class);
                startActivity(mapsActivity);
                finish();
            }
        },SPLASH_SCREEN_TIMEOUT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
