package com.example.revitalize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    protected void onStart() {
        super.onStart();

        int secondsDelayed = 2;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null)
                {
                    sendUsertoLoginActivity();

                } else if(currentUser != null)
                {
                    sendUsertoMainActivity();
                }

            }
        }, secondsDelayed * 1000);
    }

    //sends the user to the verification activity where it confirms the number
    private void sendUsertoLoginActivity(){
        Intent loginIntent = new Intent(SplashActivity.this, PhoneLoginActivity.class);
        //prevents user from using back button
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    //sends the user to the main activity where it confirms the number
    private void sendUsertoMainActivity(){
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}