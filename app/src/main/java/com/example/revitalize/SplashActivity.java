package com.example.revitalize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        int secondsDelayed = 5;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                checkuser();
            }
        }, secondsDelayed * 1000);

    }

    private void checkuser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //user is signed in
            sendUsertoMainActivity();
        } else {
            // No user is signed in
            sendUsertoLoginActivity();
        }
    }

    private void sendUsertoLoginActivity(){
        //sends the user to the verification activity where it confirms the number
        Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void sendUsertoMainActivity(){
        //sends the user to the verification activity where it confirms the number
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}