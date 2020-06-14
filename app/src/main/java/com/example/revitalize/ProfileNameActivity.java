package com.example.revitalize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ProfileNameActivity extends AppCompatActivity {

    RelativeLayout mLayout;
    AnimationDrawable mDrawable;
    private EditText username, userstatus;
    private ImageButton next_btn;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRefDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_name);

        //color animation
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mLayout = (RelativeLayout)findViewById(R.id.profile_name_activity_layout);
        mDrawable = (AnimationDrawable) mLayout.getBackground();
        mDrawable.setEnterFadeDuration(3000);
        mDrawable.setExitFadeDuration(3000);
        mDrawable.start();

        username = (EditText) findViewById(R.id.username_input);
        userstatus = (EditText) findViewById(R.id.userstatus_input);
        next_btn = (ImageButton)findViewById(R.id.next_btn);


        mAuth = FirebaseAuth.getInstance();
        RootRefDB = FirebaseDatabase.getInstance().getReference();
        currentUserID = mAuth.getCurrentUser().getUid();




        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
                FirebaseAuth.getInstance().signOut();
            }
        });
    }


    private void UpdateSettings() {
        String setUserName = username.getText().toString();
        String setUserStatus = userstatus.getText().toString();

        if(TextUtils.isEmpty(setUserName)){
            Toast.makeText(this, "Please Enter your username", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(setUserStatus)){
            Toast.makeText(this, "Please Enter a status", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", setUserName);
            profileMap.put("status", setUserStatus);
            RootRefDB.child("Users").child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendUsertoMainActivity();
                                Toast.makeText(ProfileNameActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(ProfileNameActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void sendUsertoMainActivity(){
        //sends the user to the verification activity where it confirms the number
        Intent verificationIntent = new Intent(ProfileNameActivity.this, MainActivity.class);
        verificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(verificationIntent);
        finish();
    }
}