package com.example.maxkant.puzzlepix;

// Splash activity to display logo while the main activity is loading resources.

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceStates){

        super.onCreate(savedInstanceStates);

        //Hiding the status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION, View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // Creating the intent to transition to the main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }


}
