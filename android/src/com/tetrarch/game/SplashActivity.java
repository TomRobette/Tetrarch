package com.tetrarch.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent homeIntent = new Intent(SplashActivity.this, InitActivity.class);
                SharedPreferences sharedPreferences = getPreferences( MODE_PRIVATE);
                try {
                    if (sharedPreferences.getString ("player_name", null)!=null){
                        Tetrarch.setPlayerName(sharedPreferences.getString("player_name", null));
                        homeIntent = new Intent(SplashActivity.this, AndroidLauncher.class);
                    }
                }catch (Exception e){
                    Gdx.app.log("INITIALISATION", "Erreur lors de la recherche du pseudo");
                }
                startActivity(homeIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}