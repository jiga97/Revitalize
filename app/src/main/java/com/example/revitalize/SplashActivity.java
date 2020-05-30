package com.example.revitalize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    //firebase auth object
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference RootRefDB;

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


        mAuth = FirebaseAuth.getInstance();
        RootRefDB = FirebaseDatabase.getInstance().getReference();
    }

    private void checkuser(){
         mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            //user is signed in
            VerifyUserExistance();

            sendUsertoMainActivity();
        } else {
            // No user is signed in
            sendUsertoLoginActivity();
        }
    }

    //checks if user exists
    private void VerifyUserExistance() {
        String currentUserID = mAuth.getCurrentUser().getUid();
        RootRefDB.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists())){
                    Toast.makeText(SplashActivity.this, "Welcome", Toast.LENGTH_SHORT);
                }
                else{
                    sendUsertoSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //sends the user to the verification activity where it confirms the number
    private void sendUsertoLoginActivity(){
        Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
        //prevents user from using back button
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    //sends the user to the verification activity where it confirms the number
    private void sendUsertoMainActivity(){
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    //sends the user to the settings activity where it confirms the number
    private void sendUsertoSettingsActivity(){
        Intent settingsIntent = new Intent(SplashActivity.this, SettingsActivity.class);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();
    }
}