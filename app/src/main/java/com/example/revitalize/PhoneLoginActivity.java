package com.example.revitalize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    RelativeLayout mLayout;
    AnimationDrawable mDrawable;

    private ImageButton SendVerificationCodeButton, VerifyButton;
    private EditText InputPhoneNumber, InputVerificationNumber;
    private CountryCodePicker CountryCodePicker;
    private String PhoneNumber;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference RootRefDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        //background animation
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mLayout = (RelativeLayout)findViewById(R.id.phone_login_activity_layout);
        mDrawable = (AnimationDrawable) mLayout.getBackground();
        mDrawable.setEnterFadeDuration(3000);
        mDrawable.setExitFadeDuration(3000);
        mDrawable.start();


        SendVerificationCodeButton = (ImageButton) findViewById(R.id.send_verification_code_btn);
        VerifyButton = (ImageButton) findViewById(R.id.verify_btn);
        InputPhoneNumber = (EditText) findViewById(R.id.phone_number_input);
        InputVerificationNumber = (EditText) findViewById(R.id.verification_code_input);
        CountryCodePicker = (CountryCodePicker) findViewById(R.id.country_code_picker);
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        RootRefDB = FirebaseDatabase.getInstance().getReference();

        SendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneNumber = CountryCodePicker.getSelectedCountryCodeWithPlus() + InputPhoneNumber.getText().toString();


                if(TextUtils.isEmpty(PhoneNumber))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please Enter Phone Number", Toast.LENGTH_SHORT).show();
                    ShowPhoneNumberUtils();
                }
                else
                {

                    loadingBar.setTitle("Phone verification");
                    loadingBar.setMessage("Please Wait, While authenticating your Phone");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            PhoneNumber,        // Phone number to verify
                            10,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks
                }


            }
        });

        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowVerificationUtils();
                String verificationCode = InputVerificationNumber.getText().toString();
                if(TextUtils.isEmpty(verificationCode))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please enter Verification Code", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    loadingBar.setTitle("Verifying");
                    loadingBar.setMessage("Please Wait, While we verify your Code");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
            {
                //verification method completed successfully
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e)
            {
                //if user enters wrong verification code
                // verification method failed
                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                ShowPhoneNumberUtils();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token)
            {
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                loadingBar.dismiss();

                Toast.makeText(PhoneLoginActivity.this, "Code has been Sent.", Toast.LENGTH_SHORT).show();
                ShowVerificationUtils();
            }
        };

    }


    //shows the input field and utils required for the phone number process
    private void ShowPhoneNumberUtils()
    {

        SendVerificationCodeButton.setVisibility(View.VISIBLE);
        InputPhoneNumber.setVisibility(View.VISIBLE);
        CountryCodePicker.setVisibility(View.VISIBLE);

        VerifyButton.setVisibility(View.INVISIBLE);
        InputVerificationNumber.setVisibility(View.INVISIBLE);
    }

    //shows the input field and utils required for the verification process
    private void ShowVerificationUtils()
    {
        SendVerificationCodeButton.setVisibility(View.INVISIBLE);
        InputPhoneNumber.setVisibility(View.INVISIBLE);
        CountryCodePicker.setVisibility(View.INVISIBLE);

        VerifyButton.setVisibility(View.VISIBLE);
        InputVerificationNumber.setVisibility(View.VISIBLE);
    }


    //phone verification sign in
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        String currentUserID = mAuth.getCurrentUser().getUid();
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "SignIn Successful", Toast.LENGTH_SHORT).show();
                            RootRefDB.child("Users").child(currentUserID).setValue("");
                            sendUsertoProfileNameActivity();

                        }
                        else
                        {
                            String errorMessage = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error" + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //sends the user to the settings activity where it confirms the number
    private void sendUsertoProfileNameActivity(){
        Intent profileNameIntent = new Intent(PhoneLoginActivity.this, ProfileNameActivity.class);
        profileNameIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileNameIntent);
        finish();
    }

}