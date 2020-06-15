package com.example.revitalize;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileAvatarActivity extends AppCompatActivity {

    RelativeLayout mLayout;
    AnimationDrawable mDrawable;

    private ImageView mCameraLogo;
    private ImageButton mSaveAvatar;
    private CircleImageView mAvatarView;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference RootRefDB;
    private StorageReference userProfileImageRefDB;
    private Uri avatarUri;

    private static final int GALLERY_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_avatar);

        //color animation
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mLayout = (RelativeLayout)findViewById(R.id.profile_avatar_activity_layout);
        mDrawable = (AnimationDrawable) mLayout.getBackground();
        mDrawable.setEnterFadeDuration(3000);
        mDrawable.setExitFadeDuration(3000);
        mDrawable.start();

        mCameraLogo = (ImageView)findViewById(R.id.profile_avatar_activity1);
        mSaveAvatar = (ImageButton)findViewById(R.id.save_btn);
        mAvatarView = (CircleImageView)findViewById(R.id.profile_avatar_activity);
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        RootRefDB = FirebaseDatabase.getInstance().getReference();
        currentUserID = mAuth.getCurrentUser().getUid();
        userProfileImageRefDB = FirebaseStorage.getInstance().getReference().child("Profile Images");



        mCameraLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetImage(ProfileAvatarActivity.this);
            }
        });

        mAvatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetImage(ProfileAvatarActivity.this);
            }
        });

        mSaveAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadAvatar();
            }
        });

    }

    private void GetImage(Context context)
    {
        CropImage.activity().start(ProfileAvatarActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK)
            {
                avatarUri = result.getUri();
                mAvatarView.setImageURI(avatarUri);

            }
            else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception e = result.getError();
                Toast.makeText(this, "Error" + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void UploadAvatar() {
        //loading bar
        loadingBar.setTitle("Set profile image");
        loadingBar.setMessage("Please wait while we upload your image");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


        final StorageReference filepath = userProfileImageRefDB.child(currentUserID + ".jpg");
        final UploadTask uploadTask = filepath.putFile(avatarUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return filepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful())
                {
                    Uri downloadUri = task.getResult();
                    Picasso.get().load(downloadUri).placeholder(R.drawable.profile_image).into(mAvatarView);
                } else
                {
                    Toast.makeText(ProfileAvatarActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void sendUsertoMainActivity(){
        //sends the user to the verification activity where it confirms the number
        Intent mainIntent = new Intent(ProfileAvatarActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}