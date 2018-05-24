package com.shubham.tripin1.offeehandler;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shubham.tripin1.offeehandler.Managers.SharedPrefManager;

public class SplashActivity extends AppCompatActivity {

    private SharedPrefManager sharedPrefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPrefManager = new SharedPrefManager(this);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!sharedPrefManager.getUserHpass().isEmpty()){
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(SplashActivity.this,RagActivity.class));
                    finish();
                }

            }
        }, 2000);
    }
}
