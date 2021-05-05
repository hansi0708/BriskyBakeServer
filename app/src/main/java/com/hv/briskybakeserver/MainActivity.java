package com.hv.briskybakeserver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        View v=getWindow().getDecorView();
        int u=View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN;
        v.setSystemUiVisibility(u);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(){
            public void run(){
                try{
                    sleep(5*1000);
                    Intent i=new Intent(getBaseContext(),SignIn.class);
                    startActivity(i);

                    finish();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        };thread.start();


    }
}