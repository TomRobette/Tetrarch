package com.tetrarch.game;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainScreenActivity extends AppCompatActivity {
    private Button btPlay, btHost, btOptions;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        initView();
        initBt();
    }

    private void initView(){
        btPlay = findViewById(R.id.btPlay);
        btHost = findViewById(R.id.btHost);
        btOptions = findViewById(R.id.btOptions);
        name = findViewById(R.id.name);
        if (Tetrarch.pseudo!=null)
            name.setText(Tetrarch.pseudo);
    }

    private void initBt(){
        btPlay.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(MainScreenActivity.this, ConnectActivity.class));
                    finish();
                }catch (Exception e){}
            }
        });
        btHost.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {

                }catch (Exception e){}
            }
        });
        btOptions.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {

                }catch (Exception e){}
            }
        });
    }

}