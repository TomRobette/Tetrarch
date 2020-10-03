package com.tetrarch.game;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class InitActivity extends AppCompatActivity {
    private Button btValider;
    private TextView text, alertText;
    private EditText textZone;
    private FrameLayout alertZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        initView();
        initBt();
    }

    private void initView(){
        btValider = findViewById(R.id.button);
        text = findViewById(R.id.textView);
        textZone = findViewById(R.id.editTextTextPersonName);
        alertZone = findViewById(R.id.alertFrame);
        alertText = findViewById(R.id.alert);
    }

    private void initBt(){
        btValider.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                alertZone.setVisibility(View.INVISIBLE);
                try {
                    String pseudo = textZone.getText().toString();
                    Tetrarch.setPlayerName(pseudo);
                    startActivity(new Intent(InitActivity.this, AndroidLauncher.class));
                    finish();
                }catch (Exception e){
                    alertText.setText("Error while saving name");
                    alertZone.setVisibility(View.VISIBLE);
                }

            }
        });
    }

}